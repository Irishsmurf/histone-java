<!DOCTYPE HTML>
<html{{if not this.page.prefs.maximized}} class="main-pages"{{/if}} lang="ru">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<title>Мегафон - {{page.prefs.title}}</title>
		<link rel="apple-touch-icon-precomposed" sizes="114x114" href="/templates/main/media/touch-icon-1.png" />
		<link rel="apple-touch-icon-precomposed" sizes="72x72" href="/templates/main/media/touch-icon-2.png" />
		<link rel="apple-touch-icon-precomposed" href="/templates/login/img/touch-icon-3.png" />
		<meta name="msapplication-TileImage" content="/templates/main/media/metro-logo.png" />
		<meta name="msapplication-TileColor" content="#ffffff" />
		<link rel="icon" type="image/x-icon" href="/favicon.ico" />
		<link rel="shortcut icon" type="image/x-icon" href="/favicon.ico" />
		<meta name="SKYPE_TOOLBAR" content="SKYPE_TOOLBAR_PARSER_COMPATIBLE" />
		<meta name="format-detection" content="telephone=no" />
		<meta name="format-detection" content="address=no" />
		<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" />
		<link rel="stylesheet" type="text/css" href="style/stylesheet.css?02082013" />
		{{import "static:///templates/snippets.tpl"}}
		{{import "static:///script/ui/ui.tpl"}}
		{{renderGadgetResources("text/css")}}
		<!--[if lt IE 9]>
			<link rel="stylesheet" type="text/css" href="style/stylesheet-ie8.css?02082013" />
		<![endif]-->
	</head>
	<body>

		{{if cfg.showVersion}}
			<div style="position:fixed;bottom:1px;left:1px;z-index:1000000">
				<div style="padding:3px 5px;background-color:#e87100;color:#FFF;font-size:9px;line-height:normal">
                    <span style="white-space:nowrap">Version: {{debugInfo.version['lk.webapp.version']}}</span> <br/> <span style="white-space:nowrap">Request ID: {{debugInfo.requestId}}</span>
				</div>
			</div>
		{{/if}}

		<div class="page-menu">
			<div class="content-centered menu-user-corob">
				{{if this.page.id is 'main'}}
					<!-- menu-user -->
					<div class="menu-user">

						{{var userProfile = loadJSON('pipes://lk/userProfile/get')}}
						{{var currentNumber = userProfile.currentNumber}}
						{{var numbers = userProfile.numbers.group('userId')}}
						{{var userProfile = numbers[currentNumber][0]}}

						<div class="menu-user-box menu-user-aktive">
							<div class="development"></div>
							<div class="menu-user-cell menu-user-arrow-right">
								<i></i>
							</div>
							<div class="menu-user-cell">
								<span class="menu-user-name"><b>{{userProfile.name}}</b></span>
								<span class="menu-user-number">{{phoneNumberFormat(userProfile.userId)}}</span>
							</div>
							<div class="menu-user-cell menu-user-avatar">
								<div class="menu-user-avatar-box">
								{{avatar([
									size: 50,
									avatar: userProfile.avatarId,
									alt: userProfile.name
								])}}
								</div>
							</div>
							<div class="menu-user-cell menu-user-arrow-bottom">
								<i></i>
							</div>
						</div>

						<div class="menu-user-pop">
							<div class="menu-user-all">
								<div class="menu-user-box">
									{{for userProfile in userProfile.numbers}}
										<div class="menu-user-row">
											<div class="menu-user-cell menu-user-avatar">
												<img src="/templates/main/media/avatars/50/2.png" alt="Татьяна Назарова" />
											</div>
											<div class="menu-user-cell">
												<span class="menu-user-name"><b>{{userProfile.name}}</b></span>
												<span class="menu-user-number">{{phoneNumberFormat(userProfile.userId)}}</span>
												<span class="menu-user-info">{{userProfile.status}}</span>
											</div>
										</div>
									{{/for}}
								</div>
							</div>
							<div class="menu-user-box menu-user-but">
								<div class="menu-user-cell"><a class="menu-user-setting" href="/userProfile">Настройки</a></div>
								<div class="menu-user-cell"><a class="menu-user-exit" href="/logout">Выйти</a></div>
							</div>
						</div>

					</div>
					<!-- end. menu-user -->
				{{/if}}
				<div class="box-items">
					{{if this.page.prefs.maximized}}
					<div class="box-item box-item-home">
						{{var pages = getPages(this.page.mappingUrl, -20000)}}
						<a href="{{pages[1].prefs.mainMenu ? pages[1].url : pages[0].url}}"><i></i></a>
					</div>
					{{/if}}
					<div class="box-item box-item-menu-mobile">
						<i></i>
					</div>
					<div class="box-item box-item-mobile">
						<!-- MENU -->
						{{import "static:///templates/menu.tpl"}}
						{{lkmenu(loadJSON('static:///menu.json'))}}
						<!-- END. MENU -->
					</div>


					<form method="GET" action="/search/">
						<div class="box-item box-search">
							<div class="box-item-search">
								<div class="box-items">
									<div class="box-item box-item-input">
										<div class="box-item-search-input">
											<input type="text" autocomplete="off" name="query" tabindex="-1" value="Поиск сервиса" title="Поиск сервиса" />
										</div>
										<i></i>
									</div>
									<div class="box-item box-item-close-search">
										<a href="#">Отмена</a>
									</div>
								</div>
							</div>
						</div>
						<div class="box-item box-item-dop-mobile"></div>
					</form>

				</div>
			</div>
		</div>

		<div class="dashboard-wrapper{{if this.page.prefs.maximized}} dashboard-gadget-wrapper{{/if}}">
			<div class="content-centered">

				{{var userAgent = userAgent.toLowerCase()}}
				{{var maxRows = userAgent.test('ipod|iphone') and 2 or 5}}

				{{renderDashboard(
					getPages('/'),
					this.page.mappingUrl,
					maxRows,
					this.page.prefs.maximized
				)}}

				<div class="dashboard-footer">
					<div class="content-centered menu-user-corob">

						{{if not this.page.prefs.maximized}}
							<div class="dashboard-scollbar-box">
								<div class="dashboard-scollbar">
									<div class="dashboard-scollbar-bg"></div>
									<div class="dashboard-scollbar-handle"></div>
									<div></div>
								</div>
							</div>
						{{/if}}

						{{if not this.page.prefs.maximized}}
						<div class="dashboard-footer-box">
							<div class="dashboard-footerItemLeft dashboard-footerItemLeft-width"><i></i></div>
							<a href="/feedback/" class="dashboard-footerItemLeft dashboard-footer-feedback">
								<div class="dashboard-footerS">
									Обратная связь
								</div>
							</a>
							<div class="dashboard-footerItem dashboard-footerR"></div>
							<div class="ddashboard-footerItem dashboard-footerItemRight-width"><i></i></div>
						</div>
						{{/if}}
					</div>

				</div>
			</div>
		</div>


		<div class="page-footer display-mobile">
			<div class="content-centered">
				<div class="page-footer-cols">
					<div class="page-footer-left">
						<div>
							<a href="http://www.megafon.ru/" target="_blank"><i></i></a>
						</div>
					</div>
					<div class="page-footer-center">
						<p>Контактный центр: <span class="page-footer-m">8 800 550-0500</span> Для абонентов МегаФон: <span>0500</span> </p>
					</div>
					<div class="page-footer-right">
						<div class="page-footer-icons"><!--
							--><a title="МегаФон в YouTube" href="#"><i class="yt_icon"></i></a><!--
							--><a title="МегаФон в Вконтакте" href="#"><i class="vk_icon"></i></a><!--
							--><a title="МегаФон в facebook" href="#"><i class="fb_icon"></i></a><!--
							--><a title="МегаФон в Twitter" href="#"><i class="tw_icon"></i></a><!--
							--><a title="МегаФон в LiveJournal" href="#"><i class="lj_icon"></i></a><!--
						--></div>
					</div>
				</div>
			</div>
		</div>

		<script type="text/javascript">
			var require = ({

				config: {
					'portal/Portal': {
						pipesBase: {{(this.env.pipesUrlPrefix + '/').toJSON()}}
					}
				},

				map: {
					'*': {
						'UI': 'ui/ui',
						'jQuery': 'system/jQuery',
						'Utils': 'system/Utils',
						'CachePool': 'system/CachePool',
						'Histone': 'system/Histone',
						'Portal': 'portal/Portal',
						'Dashboard': 'portal/Dashboard'
					}
				}

			});
		</script>

		<script type="text/javascript"
			src="/static/script/require.js"
			data-main="/static/script/application.js">
		</script>

		<script type="text/javascript" src="/static/script/all.js"></script>

		{{renderGadgetResources("text/javascript")}}

	</body>
</html>