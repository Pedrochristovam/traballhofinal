package br.com.sigevi.pattern.strategy;

import br.com.sigevi.model.Vistoria;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.nio.file.Path;

@Component
public class RelatorioResumidoStrategy implements RelatorioGeracaoStrategy {

    @Override
    public Path gerar(Vistoria vistoria, Path diretorioSaida) {
        Path arquivo = diretorioSaida.resolve("relatorio-resumido-v" + vistoria.getId() + ".pdf");
        try (Document document = new Document()) {
            PdfWriter.getInstance(document, new FileOutputStream(arquivo.toFile()));
            document.open();
            document.add(new Paragraph("SIGEVI - Relatorio Resumido"));
            document.add(new Paragraph("Vistoria ID: " + vistoria.getId()));
            document.add(new Paragraph("Imovel: " + vistoria.getImovel().getMatricula()));
            document.add(new Paragraph("Status: " + vistoria.getStatus()));
            document.add(new Paragraph("Data: " + vistoria.getDataVistoria()));
            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar relatorio resumido", e);
        }
        return arquivo;
    }

    @Override
    public String getTipo() {
        return "RESUMIDO";
    }
}
