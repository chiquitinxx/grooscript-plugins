def groovyTimes = { number ->
	'Groovy' * number
}

model.each {
	p groovyTimes(it)
}