<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="utf-8" />

	<link rel="apple-touch-icon-precomposed" sizes="114x114" href="/templates/login/img/touch-icon-1.png" />
	<link rel="apple-touch-icon-precomposed" sizes="72x72" href="/templates/login/img/touch-icon-2.png" />
	<link rel="apple-touch-icon-precomposed" href="/templates/login/img/touch-icon-3.png" />

	<meta name="msapplication-TileImage" content="/templates/login/img/metro-logo.png" />
	<meta name="msapplication-TileColor" content="#ffffff" />

	<link rel="icon" type="image/x-icon" href="/favicon.ico" />
	<link rel="shortcut icon" type="image/x-icon" href="/favicon.ico" />

	<meta name="SKYPE_TOOLBAR" content="SKYPE_TOOLBAR_PARSER_COMPATIBLE" />
	<meta name="format-detection" content="telephone=no" />
	<meta name="format-detection" content="address=no" />

	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" />

    <title>Мегафон - {{page.prefs.title}}</title>
    <link rel="stylesheet" href="css/style.css" />
    <!--[if (IE 7)|(IE 8)]><style>html,body{overflow:auto}.box{width:978px}</style><![endif]-->
    <script src="js/jquery-1.8.1.min.js"></script>
    <script src="js/jquery.mousewheel.js"></script>
    <script src="js/jquery.jscrollpane.min.js"></script>
    <script src="js/js.js"></script>
</head>
<body>
{{if cfg.showVersion}}
	<div style="position:fixed;bottom:1px;left:1px;z-index:1000000">
		<div style="padding:3px 5px;background-color:#e87100;color:#FFF;font-size:9px;line-height:normal">
			<span style="white-space:nowrap">Version: {{debugInfo.version['lk.webapp.version']}}</span> <br/> <span style="white-space:nowrap">Request ID: {{debugInfo.requestId}}</span>
		</div>
	</div>
{{/if}}
<div class="popup-box">
	<div class="popup"></div>
	<div class="popup-info">
		<div class="box-content text-info">
			<div class="text-box">
				<i class="close-box-popup"></i>
				<h2>ДЛЯ ПОЛУЧЕНИЯ ПАРОЛЯ НАБЕРИТЕ USSD-КОМАНДУ:</h2>
				<span class="ussd"></span>
			</div>
		</div>
	</div>
</div>
<div class="main">
	<div class="multisite">
		<div class="box">

			<a class="link-menu" href="#"></a>


			<div class="logo"><a href="/" title="«МегаФон» - оператор сотовой связи"><span>«МегаФон» - оператор сотовой связи</span></a></div>

					<span id="select" class="menu-select foot close">
						<span class="input-box">
							<span class="input-style">
								<span class="input-style-r-but">
									<span class="input-style-c">
										<span>Частным клиентам</span>
									</span>
								</span>
							</span>
						</span>
						<ul class="t-multisite">
							<li class="small"><a href="http://moscow.megafon.ru/corporate/">Корпоративным клиентам</a></li>
							<li class="float"><span>Частным клиентам</span></li>
						</ul>
					</span>
			<div class="search-block">
				<form action="#" method="get">
							<span class="input-style">
								<span class="input-style-r">
									<span class="input-style-c input-style-small">
										<input type="text" name="text" placeholder="поиск">
									</span>
								</span>
							</span>
					<input type="submit" class="button" value="">
				</form>
			</div>
			<div class="mature-rating">18+</div>

		</div>
	</div>
	<div class="box">

		<div class="top">
			<a class="logo-big" title="«МегаФон» - оператор сотовой связи"></a>
		</div>
		{{import "static:///templates/snippets.tpl"}}

		{{* MENU *}}
		<link rel="stylesheet" href="/templates/login/menu/menu.css">
		<!--[if (IE 7)|(IE 8)]>
			<link rel="stylesheet" href="/templates/login/menu/menu-ie.css">
		<![endif]-->
		{{import "static:///templates/menu.tpl"}}
		{{menu(loadJSON('static:///menu.json'))}}
		<script src="/templates/login/menu/menu.js"></script>
		<!--[if (IE 7)|(IE 8)]>
			<script src="/templates/login/menu/menu-ie.js"></script>
		<![endif]-->
		{{* END MENU *}}

		{{if this.page.prefs.action is 'login'}}
			{{var csrf = form.csrf}}
			{{var captcha = form.captchaRequired is '' ? false : form.captchaRequired}}
			{{var pipesUrl = env.pipesUrlPrefix}}
			{{var messagesError = form.login.error is '' ? '' : form.login.error}}
			{{var oper = env.operCode is '' or env.operCode is 'null' ? 'center' : env.operCode}}
			{{var region = env.regionCode is '' or env.regionCode is 'null' ? 'moscow' : env.regionCode}}
			{{var regionsList = env.regions}}
			{{var nomer = post.j_username}}
			{{var password = post.j_password}}
				{{include('form.tpl', [csrf: csrf, captcha: captcha, pipesUrl: pipesUrl, messagesError: messagesError, operCode: oper, regionCode: region, RegionList: regionsList, nomer: nomer, password: password, DEBUG: cfg.DEBUG])}}
		{{elseif this.page.prefs.action is 'select'}}
			{{var lk = form.lkUrl}}
			{{var sg = form.sgUrl}}
				{{include('select.tpl', [lkUrl: lk, sgUrl: sg])}}
		{{elseif this.page.prefs.action is 'logout'}}
				{{include('logout.tpl')}}
		{{elseif this.page.prefs.action is 'sessionExpired'}}
				{{include('sessionExpired.tpl')}}
		{{elseif this.page.prefs.action is 'changePassword'}}

				{{var csrf = form.csrf}}
				{{var redirectUrl = form.changePassword.redirectUrl is '' ? '' : form.changePassword.redirectUrl}}
				{{var errorName = form.changePassword.errorName is '' ? '' : form.changePassword.errorName}}

				{{include('changePassword.tpl', [csrf: csrf, redirectUrl: redirectUrl, errorName: errorName])}}
		{{/if}}

	</div>
	<!-- footer -->
	<div class="footer">
		<div class="box">
			<div class="f-box-phone">
				<div class="f-box-center">

					<div class="phone">

						<div class="phone-mini-box">
							<h3><span>0500<span></h3><span class="phone-mini">Автоматический<br>голосовой помощник</span>
						</div>

						<h4><span>8 800 550-0500</span></h4>
						<p>Справочная служба</p>

						<div class="social-links"><!--
								--><a title="youtube" class="yt_ic" href="http://www.youtube.com/user/megafontv"></a><!--
								--><a title="вконтакте" class="vk_ic" href="http://vkontakte.ru/megafon"></a><!--
								--><a title="facebook" class="fb_ic" href="http://www.facebook.com/megafon.ru?v=wall"></a><!--
								--><a title="twitter" class="tw_ic" href="https://twitter.com/megafoncorp"></a><!--
								--><a title="habrahabr" class="hb_ic" href="http://habrahabr.ru/company/megafon/"></a><!--
								--><a title="livejournal" class="lj_ic" href="http://community.livejournal.com/ru_megafon/"></a><!--
							--></div>

					</div>

					<div class="counter">

					<h4>64 742 073</h4>
					<p>	абонента в апреле</p>
					</div>
					<div class="copyright">
						<p>Сайт является средством массовой информации.
						<br>Номер свидетельства: Эл № ФС 77 - 24991.
						<br>Дата регистрации: 30.06.2006.
						<br>
						<br>© 2013 ОАО «МегаФон»</p>
					</div>

				</div>
			</div>

			<div class="f-group">

				<ul class="f-menu f-box">
					<li><a href="http://moscow.megafon.ru/feedback/">Обратная связь</a></li>
					<li><a href="http://moscow.megafon.ru/map/">Карта сайта</a></li>
					<li><a href="http://moscow.megafon.ru/usloviya_okazaniya/">Условия оказания услуг</a></li>
					<li><a href="http://www.megafon.ru/mobile.action?mobile">Мобильная версия</a></li>
					<li><a href="http://corp.megafon.ru/investors/management/docs/litsenzii/">Лицензии</a></li>
					<li><a href="http://moscow.megafon.ru/partners/dealers/">Партнёрам</a></li>
					<li><a href="http://english.moscow.megafon.ru/"><b>in English</b></a></li>
				</ul>
				<ul class="f-menu f-box">
				    <li><a href="http://moscow.corp.megafon.ru/">О компании</a></li>
					<li><a href="http://moscow.corp.megafon.ru/press/information/">Новости</a></li>
					<li><a href="http://msk.corp.megafon.ru/investors/">Инвесторам/Investors</a></li>
					<li><a href="http://msk.corp.megafon.ru/press/">Прессе</a></li>
					<li><a href="http://msk.corp.megafon.ru/innovation/">Инновации</a></li>
					<li><a href="http://msk.corp.megafon.ru/sotsialnaya_otvetstvennost/">Социальная ответственность</a></li>
					<li><a href="http://msk.corp.megafon.ru/work/">Работа в МегаФоне</a></li>
				</ul>

			</div>

		</div>
	</div>

</div>
</body>
</html>