{{var f = "world"}}
        {{var numbers = [1,2,3,4,5]}}
        {{for i in numbers}}
        {{for x in numbers}}
        {{var a = "Hello"}}
        {{a}},{{f}} {{self.index}} {{self.last}} {{self.qwe}}
        {{/for}}

        {{for y in [numbers]}}
        {{var a = "Hello"}}
        {{a}},{{f}} {{i}} {{self.last}} {{self.index}}
        {{/for}}

        {{for i in [numbers]}}
        {{var a = "Hello"}}
        {{a}},{{f}} {{i}} {{self.last}} {{self.index}}
        {{/for}}
        {{/for}}