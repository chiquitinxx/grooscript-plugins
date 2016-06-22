h1 model.title
ul {
	model.books.each {
		p "Title: ${it.title} Author: ${it.author}"
	}
}