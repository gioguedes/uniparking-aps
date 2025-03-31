package com.trabalhointeligencia.uniparking.controller;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.trabalhointeligencia.uniparking.models.Registro;
import com.trabalhointeligencia.uniparking.repository.UsuarioRepository;
import com.trabalhointeligencia.uniparking.services.RegistroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/registros")
public class RegistroController {

    @Autowired
    private RegistroService registroService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public String listarRegistros(Model model, @RequestParam("page") java.util.Optional<Integer> page) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        List<Registro> all = registroService.listarRegistros();
        List<Registro> filtered = all.stream()
                .filter(reg -> reg.getVaga() != null && reg.getVaga().getEstacionamento() != null
                        && reg.getVaga().getEstacionamento().getUsuario() != null
                        && reg.getVaga().getEstacionamento().getUsuario().getLogin().equals(username))
                .collect(Collectors.toList());
        int currentPage = page.orElse(1);
        int pageSize = 10;
        int start = (currentPage - 1) * pageSize;
        int end = Math.min(start + pageSize, filtered.size());
        List<Registro> subList = filtered.subList(start, end);
        Page<Registro> registroPage = new PageImpl<>(subList, PageRequest.of(currentPage - 1, pageSize), filtered.size());
        model.addAttribute("registroPage", registroPage);
        int totalPages = registroPage.getTotalPages();
        if(totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        return "admin";
    }

    @GetMapping("/excluir/{id}")
    public String excluirRegistro(@PathVariable("id") Integer id) {
        registroService.excluirRegistro(id);
        return "redirect:/registros";
    }

    @GetMapping("/download")
    public void downloadPdf(HttpServletResponse response) throws DocumentException, IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=registros.pdf");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        List<Registro> all = registroService.listarRegistros();
        List<Registro> filtered = all.stream()
                .filter(reg -> reg.getVaga() != null && reg.getVaga().getEstacionamento() != null
                        && reg.getVaga().getEstacionamento().getUsuario() != null
                        && reg.getVaga().getEstacionamento().getUsuario().getLogin().equals(username))
                .collect(Collectors.toList());
        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph title = new Paragraph("Uniparking - Registros", titleFont);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph(" "));
        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        PdfPCell hcell;
        hcell = new PdfPCell(new Paragraph("Nome Cliente", headFont));
        table.addCell(hcell);
        hcell = new PdfPCell(new Paragraph("Placa", headFont));
        table.addCell(hcell);
        hcell = new PdfPCell(new Paragraph("Vaga", headFont));
        table.addCell(hcell);
        hcell = new PdfPCell(new Paragraph("Data Entrada", headFont));
        table.addCell(hcell);
        hcell = new PdfPCell(new Paragraph("Data Saída", headFont));
        table.addCell(hcell);
        hcell = new PdfPCell(new Paragraph("Valor Cobrado", headFont));
        table.addCell(hcell);
        hcell = new PdfPCell(new Paragraph("Duração (minutos)", headFont));
        table.addCell(hcell);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (Registro reg : filtered) {
            String nomeCliente = (reg.getVeiculo() != null && reg.getVeiculo().getCliente() != null)
                    ? reg.getVeiculo().getCliente().getNome() : "N/A";
            String placa = (reg.getVeiculo() != null) ? reg.getVeiculo().getPlaca() : "N/A";
            String vaga = (reg.getVaga() != null) ? reg.getVaga().getNumeroVaga() : "N/A";
            String dataEntrada = (reg.getDataEntrada() != null) ? reg.getDataEntrada().format(dtf) : "N/A";
            String dataSaida = (reg.getDataSaida() != null) ? reg.getDataSaida().format(dtf) : "Em aberto";
            String valorCobrado = (reg.getValorCobrado() != null) ? reg.getValorCobrado().toString() : "N/A";
            String duracao = (reg.getDuracao() != null) ? reg.getDuracao().toString() : "N/A";

            table.addCell(nomeCliente);
            table.addCell(placa);
            table.addCell(vaga);
            table.addCell(dataEntrada);
            table.addCell(dataSaida);
            table.addCell(valorCobrado);
            table.addCell(duracao);
        }
        document.add(table);
        document.close();
    }

}
