package com.manura.foodapp.OrderService.Utils;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.security.SecureRandom;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;

@Service
public class Utils {

    private final Random RANDOM = new SecureRandom();
    private final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    public String generateId(int length) {
        return generateRandomString(length);
    }

    public String generateAddressId(int length) {
        return generateRandomString(length);
    }

    private String generateRandomString(int length) {
        StringBuilder returnValue = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            returnValue.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }

        return new String(returnValue);
    }
    
    private ByteArrayOutputStream generatePdf(String html,String name) {
		PdfWriter pdfWriter = null;
		Document document = new Document();
		try {

			document = new Document();
			// document header attributes
			document.addAuthor("Manura");
			document.addCreationDate();
			document.addProducer();
			document.addCreator("Manura");
			document.addTitle(name);
			document.setPageSize(PageSize.LETTER);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PdfWriter.getInstance(document, baos);
			document.open();

			XMLWorkerHelper xmlWorkerHelper = XMLWorkerHelper.getInstance();
			xmlWorkerHelper.getDefaultCssResolver(true);
			xmlWorkerHelper.parseXHtml(pdfWriter, document, new StringReader(
					html));
			document.close();
			return baos;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
    
    public byte[] getAllBytesPdf(String html,String name) {
    	return generatePdf(html, name).toByteArray();
    }

    
   
//    public Document generatePdfFromHtml(String html) throws IOException, DocumentException {
//		ITextRenderer renderer = new ITextRenderer();
//		renderer.setDocumentFromString(html);
//		renderer.layout();
//		Document document = renderer.getDocument();
//		return document;
//	}
	
//	public byte[] asByteArray(Document doc)throws TransformerException {
//		TransformerFactory transformerFactory = TransformerFactory
//	            .newInstance();
//        Transformer transformer = null;
//        transformer = transformerFactory.newTransformer();
//        ByteArrayOutputStream bout = new ByteArrayOutputStream();
//        StreamResult result = new StreamResult(bout);
//        DOMSource source = new DOMSource(doc);
//        transformer.transform(source, result);
//        return bout.toByteArray();
//    }

    
}
