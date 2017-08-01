/*
 * Copyright (c) 2017 Sergio Gonçalves Puntar Filho
 * 
 * This program is made available under the terms of the MIT License.
 * See the LICENSE file for details.
 */
package br.com.sgpf.common.domain.dataimport.impl;

import br.com.sgpf.common.domain.dataimport.DataImportItem;
import br.com.sgpf.common.domain.vo.SimpleDataElement;

public class BaseDataImporterImpl extends BaseDataImporter<Integer, SimpleDataElement> {
	private static final long serialVersionUID = 1L;

	@Override
	protected void importData(DataImportItem<Integer, SimpleDataElement> item) {
		
	}
}