{{*We consider that array value should be inlined instead of 'a' reference*}}

        {{var a = [1, 2, 3]}}
        {{for t in a}}
        Hello world
        {{/for}}