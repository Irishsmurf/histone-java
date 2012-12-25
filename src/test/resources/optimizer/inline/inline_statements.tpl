{{*In result AST variable b should have inlined expression (5+2+2-(5+2)*2) *}}

        {{var a = 5+2}}
        {{var b = a+2-a*2}}