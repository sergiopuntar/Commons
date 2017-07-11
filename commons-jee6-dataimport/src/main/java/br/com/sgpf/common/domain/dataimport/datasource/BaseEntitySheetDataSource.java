package br.com.sgpf.common.domain.dataimport.datasource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import br.com.sgpf.common.domain.dataimport.EntityDataSource;
import br.com.sgpf.common.domain.dataimport.EntityImportItem;
import br.com.sgpf.common.domain.dataimport.exception.EntityDataSourceException;
import br.com.sgpf.common.domain.entity.Entity;

public abstract class BaseEntitySheetDataSource<T extends Serializable, E extends Entity<? extends Serializable>> implements EntityDataSource<T, E> {
	private static final long serialVersionUID = -7387063988593887736L;

	private static enum Type { FILE, INPUT_STREAM };
	
	private File file;
	private InputStream is;
	private Type type;
	private int sheetId;
	
	private Workbook workbook;
	private Sheet sheet;
	private int currRow;
	
	private BaseEntitySheetDataSource(Type type, int sheetId) {
		super();
		this.type = type;
		this.sheetId = sheetId;
	}
	
	public BaseEntitySheetDataSource(File file, int sheetId) {
		this(Type.FILE, sheetId);
		this.file = file;
	}
	
	public BaseEntitySheetDataSource(InputStream is, int sheetId) {
		this(Type.INPUT_STREAM, sheetId);
		this.is = is;
	}

	@Override
	public void open() throws EntityDataSourceException {
		switch (type) {
		case FILE:
			workbook = openFromFile();
			break;
		case INPUT_STREAM:
			workbook = openFromInputStream();
			break;
		default:
			throw new EntityDataSourceException();
		}
		
		sheet = workbook.getSheetAt(sheetId);
	}

	private Workbook openFromFile() throws EntityDataSourceException {
		try {
			return WorkbookFactory.create(file);
		} catch (EncryptedDocumentException e) {
			// TODO Criar exceção e mensagem especifica 
			throw new EntityDataSourceException(e);
		} catch (InvalidFormatException e) {
			// TODO Criar exceção e mensagem especifica
			throw new EntityDataSourceException(e);
		} catch (IOException e) {
			// TODO Criar exceção e mensagem especifica
			throw new EntityDataSourceException(e);
		}
	}

	private Workbook openFromInputStream() throws EntityDataSourceException {
		try {
			return WorkbookFactory.create(is);
		} catch (EncryptedDocumentException e) {
			// TODO Criar exceção e mensagem especifica
			throw new EntityDataSourceException(e);
		} catch (InvalidFormatException e) {
			// TODO Criar exceção e mensagem especifica
			throw new EntityDataSourceException(e);
		} catch (IOException e) {
			// TODO Criar exceção e mensagem especifica
			throw new EntityDataSourceException(e);
		}
	}

	@Override
	public boolean isReadable() {
		return false;
	}

	@Override
	public boolean isWritable() {
		return false;
	}

	public Sheet getSheet() {
		if (sheet == null) {
			// TODO arremessar exceção
		}
		
		return sheet;
	}

	@Override
	public boolean hasNext() throws EntityDataSourceException {
		return false;
	}

	@Override
	public EntityImportItem<T, E> getNext() throws EntityDataSourceException {
		//TODO especializar exceções nas assinaturas
		return null;
	}

	@Override
	public void sync(EntityImportItem<T, E> item) throws EntityDataSourceException {
		
	}

	@Override
	public void close() throws EntityDataSourceException {
		try {
			workbook.close();
			sheet = null;
		} catch (IOException e) {
			//TODO Criar exceção e mensagem especifica
			throw new EntityDataSourceException(e);
		}
	}
}
