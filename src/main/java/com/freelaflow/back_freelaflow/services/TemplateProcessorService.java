package com.freelaflow.back_freelaflow.services;

import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Service
public class TemplateProcessorService {

    /**
     * Processa um template DOCX substituindo variáveis pelos valores fornecidos.
     * Variáveis devem estar no formato {NOME_VARIAVEL}
     *
     * @param templatePath Caminho do arquivo template
     * @param outputPath Caminho onde o arquivo processado será salvo
     * @param variables Mapa com as variáveis e seus valores
     */
    public void processTemplate(Path templatePath, Path outputPath, Map<String, String> variables) throws IOException {
        try (FileInputStream fis = new FileInputStream(templatePath.toFile());
             XWPFDocument document = new XWPFDocument(fis)) {

            // Substitui variáveis em parágrafos
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                replacePlaceholdersInParagraph(paragraph, variables);
            }

            // Substitui variáveis em tabelas
            for (XWPFTable table : document.getTables()) {
                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        for (XWPFParagraph paragraph : cell.getParagraphs()) {
                            replacePlaceholdersInParagraph(paragraph, variables);
                        }
                    }
                }
            }

            // Salva o documento processado
            try (FileOutputStream fos = new FileOutputStream(outputPath.toFile())) {
                document.write(fos);
            }
        }
    }

    /**
     * Substitui placeholders em um parágrafo
     */
    private void replacePlaceholdersInParagraph(XWPFParagraph paragraph, Map<String, String> variables) {
        List<XWPFRun> runs = paragraph.getRuns();
        if (runs == null) {
            return;
        }

        // Reconstrói o texto do parágrafo
        StringBuilder paragraphText = new StringBuilder();
        for (XWPFRun run : runs) {
            String text = run.getText(0);
            if (text != null) {
                paragraphText.append(text);
            }
        }

        String text = paragraphText.toString();

        // Substitui todas as variáveis
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            String placeholder = "{" + entry.getKey() + "}";
            String value = entry.getValue() != null ? entry.getValue() : "";
            text = text.replace(placeholder, value);
        }

        // Se houve mudanças, atualiza o parágrafo
        if (!text.equals(paragraphText.toString())) {
            // Remove todos os runs existentes
            for (int i = runs.size() - 1; i >= 0; i--) {
                paragraph.removeRun(i);
            }

            // Adiciona o texto processado em um novo run
            XWPFRun newRun = paragraph.createRun();
            newRun.setText(text);
        }
    }

    /**
     * Extrai todas as variáveis encontradas em um template DOCX
     *
     * @param templatePath Caminho do arquivo template
     * @return Lista de variáveis encontradas (sem as chaves)
     */
    public List<String> extractVariables(Path templatePath) throws IOException {
        try (FileInputStream fis = new FileInputStream(templatePath.toFile());
             XWPFDocument document = new XWPFDocument(fis)) {

            java.util.Set<String> variables = new java.util.HashSet<>();

            // Extrai variáveis de parágrafos
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                extractVariablesFromParagraph(paragraph, variables);
            }

            // Extrai variáveis de tabelas
            for (XWPFTable table : document.getTables()) {
                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        for (XWPFParagraph paragraph : cell.getParagraphs()) {
                            extractVariablesFromParagraph(paragraph, variables);
                        }
                    }
                }
            }

            return new java.util.ArrayList<>(variables);
        }
    }

    /**
     * Extrai variáveis de um parágrafo
     */
    private void extractVariablesFromParagraph(XWPFParagraph paragraph, java.util.Set<String> variables) {
        List<XWPFRun> runs = paragraph.getRuns();
        if (runs == null) {
            return;
        }

        StringBuilder paragraphText = new StringBuilder();
        for (XWPFRun run : runs) {
            String text = run.getText(0);
            if (text != null) {
                paragraphText.append(text);
            }
        }

        String text = paragraphText.toString();

        // Encontra todas as variáveis no formato {VARIAVEL}
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\{([^}]+)\\}");
        java.util.regex.Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            variables.add(matcher.group(1));
        }
    }
}
