/*
 * Copyright (c) 2017 Sergio Gonçalves Puntar Filho
 * 
 * This program is made available under the terms of the MIT License.
 * See the LICENSE file for details.
 */
package br.com.sgpf.common.domain.dataimport.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.google.common.collect.Lists;

import br.com.sgpf.common.domain.dataimport.DataImportInstructions;
import br.com.sgpf.common.domain.dataimport.DataImportItem;
import br.com.sgpf.common.domain.dataimport.DataImportResult;
import br.com.sgpf.common.domain.dataimport.ImportDataSource;
import br.com.sgpf.common.domain.dataimport.exception.DataImportException;
import br.com.sgpf.common.domain.vo.SimpleDataElement;

@RunWith(MockitoJUnitRunner.class)
public class BaseDataImporterTest {
	
	private BaseDataImporter<Integer, SimpleDataElement> baseDataImporter =  new BaseDataImporterImpl();
	
	@Mock
	private ImportDataSource<Integer, SimpleDataElement> dataSource;
	
	private List<DataImportItem<Integer, SimpleDataElement>> itemList;
	
	/**
	 * Configura o comportamento do DataSource mockado e a lista de itens de importação da seguinte
	 * forma:<br>
	 * Índice 0: Item com sincronização ativada e importado.<br>
	 * Índice 1: Item com sincronização desativada e importado. <br>
	 * Índice 2: Item com sincronização ativada e que sofreu erro durante a importação.<br>
	 */
	public void mockRegularDataSourceBehaviour() throws DataImportException {
		itemList = Lists.newArrayList(
				new DataImportItem<Integer, SimpleDataElement>(0, new SimpleDataElement(1L), new DataImportInstructions(false, false, true, false, false, true)),
				new DataImportItem<Integer, SimpleDataElement>(1, new SimpleDataElement(2L), new DataImportInstructions(false, false, true, false, false, false)),
				new DataImportItem<Integer, SimpleDataElement>(2, new SimpleDataElement(3L), new DataImportInstructions(false, false, true, false, false, true)),
				new DataImportItem<Integer, SimpleDataElement>(2, new SimpleDataElement(4L), new DataImportInstructions(false, false, true, false, false, true)));
		
		itemList.get(0).getResult().setStatus(DataImportResult.Status.INSERTED);
		itemList.get(1).getResult().setStatus(DataImportResult.Status.INSERTED);
		itemList.get(2).getResult().setStatus(DataImportResult.Status.ERROR);
		itemList.get(3).getResult().setStatus(DataImportResult.Status.OVERRIDDEN);
		
		final Iterator<DataImportItem<Integer, SimpleDataElement>> itemIterator = itemList.iterator();
		
		when(dataSource.hasNext()).then(new Answer<Boolean>() {
				@Override
				public Boolean answer(InvocationOnMock invocation) throws Throwable {
					return itemIterator.hasNext();
				}
			});
		
		when(dataSource.next()).then(new Answer<DataImportItem<Integer, SimpleDataElement>>() {
				@Override
				public DataImportItem<Integer, SimpleDataElement> answer(InvocationOnMock invocation) throws Throwable {
					return itemIterator.next();
				}
			});
		
		when(dataSource.sync(itemList.get(0))).thenReturn(false);
		when(dataSource.sync(itemList.get(2))).thenReturn(false);
		when(dataSource.sync(itemList.get(3))).thenReturn(true);
	}
	
	/**
	 * Teste passando DataSource nulo.
	 */
	@Test(expected = NullPointerException.class)
	public void importDataNullDataSourceTest() throws DataImportException {
		mockRegularDataSourceBehaviour();
		baseDataImporter.importData(null, false, false);
	}
	
	/**
	 * Teste padrão sem sincronização.
	 */
	@Test
	public void importDataTestNoSync() throws DataImportException {
		mockRegularDataSourceBehaviour();
		Collection<DataImportItem<Integer, SimpleDataElement>> importedItens = baseDataImporter.importData(dataSource, false, false);
		
		assertEquals(itemList, importedItens);
		assertFalse(itemList.get(0).getResult().isSynced());
		assertFalse(itemList.get(1).getResult().isSynced());
		assertFalse(itemList.get(2).getResult().isSynced());
		verify(dataSource).open();
		verify(dataSource).close();
	}
	
	/**
	 * Teste padrão com sicronização usando um DataSource não gravável.
	 */
	@Test
	public void importDataTestNoSyncNoWritable() throws DataImportException {
		mockRegularDataSourceBehaviour();
		when(dataSource.isWritable()).thenReturn(false);
		
		Collection<DataImportItem<Integer, SimpleDataElement>> importedItens = baseDataImporter.importData(dataSource, true, false);
		
		assertEquals(itemList, importedItens);
		assertFalse(itemList.get(0).getResult().isSynced());
		assertFalse(itemList.get(1).getResult().isSynced());
		assertFalse(itemList.get(2).getResult().isSynced());
		verify(dataSource).open();
		verify(dataSource).close();
	}
	
	/**
	 * Teste padrão com sicronização usando um DataSource gravável.
	 */
	@Test
	public void importDataTestNoSyncWritable() throws DataImportException {
		mockRegularDataSourceBehaviour();
		when(dataSource.isWritable()).thenReturn(true);
		
		Collection<DataImportItem<Integer, SimpleDataElement>> importedItens = baseDataImporter.importData(dataSource, true, false);
		
		assertEquals(itemList, importedItens);
		assertTrue(itemList.get(0).getResult().isSynced());
		assertFalse(itemList.get(1).getResult().isSynced());
		assertFalse(itemList.get(2).getResult().isSynced());
		verify(dataSource).open();
		verify(dataSource).close();
	}
	
	/**
	 * Testa um erro de importação durante a leitura do Data Source sem supressão de exceções.
	 */
	@Test(expected = DataImportException.class)
	public void dataSourceReadErrorWithoutSuppression() throws DataImportException {
		mockRegularDataSourceBehaviour();
		when(dataSource.next()).thenThrow(new DataImportException("Data Read Error"));
		
		baseDataImporter.importData(dataSource, true, false);
	}
	
	/**
	 * Testa um erro de importação durante a leitura do Data Source com supressão de exceções.
	 */
	@Test
	public void dataSourceReadErrorWithSuppression() throws DataImportException {
		
	}
	
	/**
	 * Testa um erro de importação durante a gravação dos dados no destino sem supressão de exceções.
	 */
	@Test(expected = DataImportException.class)
	public void destinyWriteErrorWithoutSuppression() throws DataImportException {
		mockRegularDataSourceBehaviour();
		BaseDataImporter<Integer, SimpleDataElement> localDataImporter = spy(baseDataImporter);
		when(localDataImporter.importData(dataSource, true, true)).thenThrow(new DataImportException("Data Write Error"));
		
		localDataImporter.importData(dataSource, true, false);
	}
	
	/**
	 * Testa um erro de importação durante a gravação dos dados no destino com supressão de exceções.
	 */
	@Test
	public void destinyWriteErrorWithSuppression() throws DataImportException {
		
	}
	
	/**
	 * Testa um erro de importação durante a sincronização dos dados na origem sem supressão de exceções.
	 */
	@Test(expected = DataImportException.class)
	public void dataSourceSyncErrorWithoutSuppression() throws DataImportException {
		mockRegularDataSourceBehaviour();
		when(dataSource.isWritable()).thenReturn(true);
		when(dataSource.sync(itemList.get(0))).thenThrow(new DataImportException("Data Sync Error"));
		
		baseDataImporter.importData(dataSource, true, false);
	}
	
	/**
	 * Testa um erro de importação durante a sincronização dos dados na origem com supressão de exceções.
	 */
	
	public void dataSourceSyncErrorWithSuppression() throws DataImportException {
		
	}
}
