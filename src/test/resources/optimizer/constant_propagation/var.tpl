{{var a = 5}}
        {{var b = a}}
<html>
    <head>
        {{b}}
    </head>
    {{var c = b}}
    {{c}}
    {{var d}}
    Hello, {{c}}
    {{/var}}
    {{d}}
</html>

        {{* We expect this: *}}
<html>
<head>
    5
</head>
5
Hello, 5
</html>