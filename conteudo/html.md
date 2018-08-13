# HTML

Uma aplicação web pode gerar vários tipos de conteúdos. O mais comum são as páginas HTML.

> Veja um exemplo de página HTML:

````html
<!DOCTYPE html>
<html>
<head>
  <title>Isto é um exemplo de página HTML</title>
</head>
<body>
  <h1>Isto é um cabeçalho</h1>
  <p>Isto é um parágrafo.</p>
</body>
</html>
````

Veja este [Tutorial sobre HTML](http://www.w3schools.com)

## CSS

A formatação do conteúdo das páginas HTML é feita através de folhas de estilo [CSS](http://www.w3schools.com).

> Veja um Exemplo:

````css
body {
  font-family: sans-serif;
}
h1 {
  color: darkblue;
}
````

## Bootstrap

Criar uma formatação ideal para uma aplicação é uma tarefa trabalhosa. Aspectos de usabilidade são essenciais em qualquer aplicação. Para não ter que reinventar a roda, uma boa prática é reutilizar soluções que deram certo. Aqui nós vamos usar os recursos disponibilizados pelo Twitter através de sua API [Bootstrap](https://getbootstrap.com).

> Veja um exemplo de uma página HTML usando o Bootstrap:

````html
<!DOCTYPE html>
<html>
<head>
  <title>Isto é um exemplo de página HTML</title>
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css">
  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>
</head>
<body>
<div class="container">
  <h1>Isto é um cabeçalho</h1>
  <p>Isto é um parágrafo.</p>
</div>
</body>
</html>
````

## Exercícios

1. Crie uma página HTML contendo
   + Duas imagens
   + Três links para endereços externos
   + Uma lista ordenada com pelo menos cinco itens
   + Uma tabela com pelo menos cinco linhas e trẽs colunas
   + Um formulário contendo o campo "SearchableText" que envia os dados para "http://portal.ifrn.edu.br/search"

2. Crie um arquivo CSS para a página da questão 1

3. Crie uma com os mesmos elementos qua questão 1 usando o Bootstrap.
