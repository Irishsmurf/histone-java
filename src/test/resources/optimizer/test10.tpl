{{var AVATAR_PATH = '/avatars/'}}

{{macro avatar(settings)}}
	{{var alt = settings.alt}}
	{{var class = settings.class}}
	{{var avatar = settings.avatar}}
	{{var size = settings.avatarSize}}
	{{var placeholder = settings.placeholder}}
	{{var img = avatar ? resolveURI([avatar, size].join('/'), AVATAR_PATH) : placeholder}}
	<!-- alt: {{settings.alt}}-->
	<img src="{{img}}" alt="{{alt}}" class="{{class}}" />
{{/macro}}

{{macro foo(t)}}
    Hello world{{t.alt}}
{{/macro}}

{{foo([avatarSize:75,alt:phone.name,avatar:phone.avatarId])}}

{{avatar([
    avatarSize: 75,
    alt: phone.name,
    avatar: phone.avatarId
])}}