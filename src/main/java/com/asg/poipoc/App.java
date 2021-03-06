/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.asg.poipoc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.EncryptionMode;
import org.apache.poi.poifs.crypt.Encryptor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class App {

	public static void main(String[] args) throws InvalidFormatException, IOException, GeneralSecurityException {
		App app = new App();
		Workbook wb = app.crearWorkbook();
		byte[] resultado = app.cifrarWorkbook(wb);
		app.escribirADisco(resultado);
		System.out.println("LISTO");
	}

	/**
	 * Simulamos la creación de un workbook para fines de prueba
	 * 
	 * @return
	 */
	public Workbook crearWorkbook() {
		Workbook wb = new XSSFWorkbook();
		Sheet sheet = wb.createSheet();
		Row row;
		Cell cell;
		for (int i = 0; i < 20; i++) {
			row = sheet.createRow(i);
			for (int j = 0; j < 10; j++) {
				cell = row.createCell(j);
				cell.setCellValue("Celda de prueba [" + i + "][" + j + "]");
			}
		}
		return wb;
	}

	/**
	 * Encriptamos el workbook
	 * 
	 * @param wb
	 * @throws IOException
	 * @throws InvalidFormatException
	 * @throws GeneralSecurityException
	 */
	public byte[] cifrarWorkbook(Workbook wb) throws InvalidFormatException, IOException, GeneralSecurityException {
		byte[] result = null;
		try (POIFSFileSystem fs = new POIFSFileSystem()) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			wb.write(bos);

			EncryptionInfo info = new EncryptionInfo(EncryptionMode.agile);
			Encryptor enc = info.getEncryptor();
			enc.confirmPassword("un_pass_fuerte");

			try (OPCPackage opc = OPCPackage.open(new ByteArrayInputStream(bos.toByteArray()));
					OutputStream os = enc.getDataStream(fs)) {
				// ByteArrayOutputStream boOutput = new ByteArrayOutputStream(os);
				opc.save(os);
			}
			try (ByteArrayOutputStream fos = new ByteArrayOutputStream()) {
				fs.writeFilesystem(fos);
				result = fos.toByteArray();
			}
		}
		return result;
	}

	/**
	 * Solo lo usamos para probar que se aperture correctamente en office
	 * @param datos
	 * @throws IOException 
	 */
	public void escribirADisco(byte[] datos) throws IOException {
		FileOutputStream out = new FileOutputStream(new File(System.getProperty("java.io.tmpdir")+"/workbook_cifrado.xls"));
		out.write(datos);
	}
	
}
