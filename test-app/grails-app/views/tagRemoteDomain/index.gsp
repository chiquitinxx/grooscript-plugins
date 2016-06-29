<!doctype html>
<html>
<head>
    <meta name="layout" content="main"/>
    <title>Tag remote domain tests</title>
</head>
<body>
    <grooscript:remoteModel domainClass="test.Book"/>
    <div id="insert"></div>
    <div id="list"></div>
    <div id="get"></div>
    <div id="update"></div>
    <div id="delete"></div>
    <grooscript:code>
        import test.Book

        GsHlp.onReady {
            Book.list().then { list ->
                if (!list) {
                    new Book(title: 'Title cool!', author: 'Any', pages: 245).save().then { newBook ->
                        GsHlp.selectorHtml '#insert', 'Created new book'
                        Book.list().then { books ->
                            GsHlp.selectorHtml '#list', books.size() + ' books in list.'
                        }
                        Book.get(newBook.id).then { getBoot ->
                            assert getBoot.id == newBook.id
                            GsHlp.selectorHtml '#get', 'Got same book by id.'
                        }
                        newBook.title = 'New title'
                        newBook.save().then { updatedBook ->
                            GsHlp.selectorHtml '#update', 'Updated book to title: ' + updatedBook.title
                            newBook.delete().then {
                                GsHlp.selectorHtml '#delete', 'Book deleted!'
                            }
                        }
                    }
                } else {
                    GsHlp.selectorHtml '#insert', 'Ups... not empty book list'
                }
            }
        }
    </grooscript:code>
</body>
</html>
