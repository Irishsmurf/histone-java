{{macro summFormat(number)}}
	{{var number = number.isNumber() ? number : number.toNumber()}}
	{{var prefix = number < 0 ? '-'}}
	{{var number = number.abs().toString().split('.')}}

	{{var first = number[0]}}
	{{var second = number[1]}}
	{{var second = second ? (',' + second + '0').slice(0, 3)}}

	{{var first}}{{for ch in first.split()}}{{first[self.last - self.index]}}{{/for}}{{/var}}
	{{var first}}{{for ch in first.split()}}{{if self.index mod 3 is 0}} {{/if}}{{ch}}{{/for}}{{/var}}
	{{var first}}{{for ch in first.split()}}{{first[self.last - self.index]}}{{/for}}{{/var}}

	{{prefix + first.strip() + second}}<span>р.</span>
{{/macro}}
