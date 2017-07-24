# CommonsJEE6

[![Quality Gate][sonar-quality-gate-badge]][sonar-quality-gate-link]
[![Coverage][sonar-coverage-badge]][sonar-coverage-link]
[![Technical debt ratio][sonar-technical-debt-badge]][sonar-technical-debt-link]

Biblioteca agregadora de componentes e utilitários de apoio para projetos JEE6.

## Sumário

Esse projeto é composto por diversos módulos que proveem funcionalidades comumente utilizadas em projetos JEE6.
Os módulos devem ser importados separadamente de acordo com as necessidades de cada projeto.

### Core

Contém os componentes mais básicos para um projeto JEE6, como:

* Exceções raiz
* Entidades e Repositórios JPA
* Componentes de gestão de mensagens para internacionalização

### Data Import

Contém componentes para importação de dados, incluindo implementação básica para origem de dados de planilhas.

### Test

Contém utilitários para testes de unidade e de integração.

[sonar-quality-gate-badge]: https://sonarcloud.io/api/badges/gate?key=br.com.sgpf:commons-jee6
[sonar-quality-gate-link]: http://sonarcloud.io/dashboard/index/br.com.sgpf:commons-jee6

[sonar-coverage-badge]: https://sonarcloud.io/api/badges/measure?key=br.com.sgpf:commons-jee6&metric=coverage
[sonar-coverage-link]: https://sonarcloud.io/component_measures/domain/Coverage?id=br.com.sgpf:commons-jee6

[sonar-technical-debt-badge]: https://sonarcloud.io/api/badges/measure?key=br.com.sgpf:commons-jee6&metric=sqale_debt_ratio
[sonar-technical-debt-link]: https://sonarcloud.io/component_measures/metric/sqale_index/list?id=br.com.sgpf:commons-jee6
