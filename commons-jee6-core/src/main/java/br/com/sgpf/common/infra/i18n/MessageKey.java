/*
 * Copyright (c) 2017 Sergio Gonçalves Puntar Filho
 * 
 * This program is made available under the terms of the MIT License.
 * See the LICENSE file for details.
 */
package br.com.sgpf.common.infra.i18n;

/**
 * Interface para enumerações de chaves de mensagens.
 * 
 * @author Sergio Puntar
 */
public interface MessageKey {
	
	/**
	 * Recupera o nome da chave.
	 * 
	 * @return Nome da chave
	 */
	public String getName();
}