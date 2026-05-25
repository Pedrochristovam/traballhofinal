package br.com.sigevi.pattern.strategy;

import br.com.sigevi.model.Foto;
import br.com.sigevi.model.Vistoria;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.nio.file.Path;

@Component
public class RelatorioCompletoStrategy implements RelatorioGeracaoStrategy {

    @Override
    public Path gerar(Vistoria vistoria, Path diretorioSaida) {
        Path arquivo = diretorioSaida.resolve("relatorio-completo-v" + vistoria.getId() + ".pdf");
        try (Document document = new Document()) {
            PdfWriter.getInstance(document, new FileOutputStream(arquivo.toFile()));
            document.open();
            document.add(new Paragraph("SIGEVI - Relatorio Completo"));
            document.add(new Paragraph("Vistoria ID: " + vistoria.getId()));
            document.add(new Paragraph("Imovel: " + vistoria.getImovel().getMatricula()));
            document.add(new Paragraph("Endereco: " + vistoria.getImovel().getEndereco()));
            document.add(new Paragraph("Inspetor: " + vistoria.getInspetor().getNome()));
            document.add(new Paragraph("Status: " + vistoria.getStatus()));
            document.add(new Paragraph("Observacoes: " + (vistoria.getObservacoes() != null ? vistoria.getObservacoes() : "-")));
            document.add(new Paragraph("Total de fotos: " + vistoria.getFotos().size()));
            for (Foto foto : vistoria.getFotos()) {
                document.add(new Paragraph(" - Foto: " + foto.getNomeArquivo()));
            }
            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar relatorio completo", e);
        }
        return arquivo;
    }

    @Override
    public String getTipo() {
        return "COMPLETO";
    }
}
