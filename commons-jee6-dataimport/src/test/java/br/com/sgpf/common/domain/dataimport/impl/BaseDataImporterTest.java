package br.com.sgpf.common.domain.dataimport.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.google.common.collect.Lists;

import br.com.sgpf.common.domain.dataimport.DataImportInstructions;
import br.com.sgpf.common.domain.dataimport.DataImportItem;
import br.com.sgpf.common.domain.dataimport.DataImportResult;
import br.com.sgpf.common.domain.dataimport.ImportDataSource;
import br.com.sgpf.common.domain.dataimport.exception.DataImportException;

@RunWith(MockitoJUnitRunner.class)
public class BaseDataImporterTest {
	
	private BaseDataImporter<Integer, DataElement> baseDataImporter =  new BaseDataImporter<Integer, DataElement>() {
		private static final long serialVersionUID = 1L;
		
		@Override
		protected void importData(DataImportItem<Integer, DataElement> item) {}
	};
	
	@Mock
	private ImportDataSource<Integer, DataElement> dataSource;
	
	private List<DataImportItem<Integer, BaseDataImporterTest.DataElement>> itemList;
	
	/**
	 * Configura o comportamento do DataSource mockado e a lista de itens de importação da seguinte
	 * forma:<br>
	 * Índice 0: Item com sincronização ativada e importado.<br>
	 * Índice 1: Item com sincronização desativada e importado. <br>
	 * Índice 2: Item com sincronização ativada e que sofreu erro durante a importação.<br>
	 */
	@Before
	public void before() throws Throwable {
		itemList = Lists.newArrayList(
				new DataImportItem<Integer, BaseDataImporterTest.DataElement>(0, new DataElement(1L), new DataImportInstructions(false, false, true, false, false, true)),
				new DataImportItem<Integer, BaseDataImporterTest.DataElement>(1, new DataElement(2L), new DataImportInstructions(false, false, true, false, false, false)),
				new DataImportItem<Integer, BaseDataImporterTest.DataElement>(2, new DataElement(3L), new DataImportInstructions(false, false, true, false, false, true)));
		
		itemList.get(0).getResult().setStatus(DataImportResult.Status.INSERTED);
		itemList.get(1).getResult().setStatus(DataImportResult.Status.INSERTED);
		itemList.get(2).getResult().setStatus(DataImportResult.Status.ERROR);
		
		final Iterator<DataImportItem<Integer, BaseDataImporterTest.DataElement>> itemIterator = itemList.iterator();
		
		when(dataSource.hasNext()).then(new Answer<Boolean>() {
				@Override
				public Boolean answer(InvocationOnMock invocation) throws Throwable {
					return itemIterator.hasNext();
				}
			});
		
		when(dataSource.next()).then(new Answer<DataImportItem<Integer, DataElement>>() {
				@Override
				public DataImportItem<Integer, DataElement> answer(InvocationOnMock invocation) throws Throwable {
					return itemIterator.next();
				}
			});
	}
	
	/**
	 * Teste passando DataSource nulo.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void importDataNullDataSourceTest() throws DataImportException {
		baseDataImporter.importData(null, false);
	}
	
	/**
	 * Teste sem sincronização.
	 */
	@Test
	public void importDataTestNoSync() throws DataImportException {
		Collection<DataImportItem<Integer, DataElement>> importedItens = baseDataImporter.importData(dataSource, false);
		
		assertEquals(itemList, importedItens);
		assertFalse(itemList.get(0).getResult().isSynced());
		assertFalse(itemList.get(1).getResult().isSynced());
		assertFalse(itemList.get(2).getResult().isSynced());
		verify(dataSource).open();
		verify(dataSource).close();
	}
	
	/**
	 * Teste com sicronização usando um DataSource não gravável.
	 */
	@Test
	public void importDataTestNoSyncNoWritable() throws DataImportException {
		when(dataSource.isWritable()).thenReturn(false);
		
		Collection<DataImportItem<Integer, DataElement>> importedItens = baseDataImporter.importData(dataSource, true);
		
		assertEquals(itemList, importedItens);
		assertFalse(itemList.get(0).getResult().isSynced());
		assertFalse(itemList.get(1).getResult().isSynced());
		assertFalse(itemList.get(2).getResult().isSynced());
		verify(dataSource).open();
		verify(dataSource).close();
	}
	
	/**
	 * Teste com sicronização usando um DataSource gravável.
	 */
	@Test
	public void importDataTestNoSyncWritable() throws DataImportException {
		when(dataSource.isWritable()).thenReturn(true);
		
		Collection<DataImportItem<Integer, DataElement>> importedItens = baseDataImporter.importData(dataSource, true);
		
		assertEquals(itemList, importedItens);
		assertTrue(itemList.get(0).getResult().isSynced());
		assertFalse(itemList.get(1).getResult().isSynced());
		assertFalse(itemList.get(2).getResult().isSynced());
		verify(dataSource).open();
		verify(dataSource).close();
	}
	
	class DataElement implements Serializable {
		private static final long serialVersionUID = 1L;
		
		private Long id;
		
		public DataElement(Long id) {
			super();
			this.id = id;
		}
		
		public Long getId() {
			return id;
		}
		
		public void setId(Long id) {
			this.id = id;
		}
	}
}
