{{macro say(what, whom)}}
{{what}}, {{whom}}
{{/macro}}
{{var a = "Hello"}}
{{eval("{{var b = 'world'}}{{say(a, b)}}")}}