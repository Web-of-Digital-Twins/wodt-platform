## [1.1.1](https://github.com/WebBased-WoDT/wodt-dts-platform/compare/1.1.0...1.1.1) (2023-12-23)


### Dependency updates

* **core-deps:** update dependency org.jetbrains.kotlinx:kotlinx-coroutines-core to v1.8.0-rc2 ([afa1667](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/afa16678b268be3195681f56fa353eb974fe74d9))

## [1.1.0](https://github.com/WebBased-WoDT/wodt-dts-platform/compare/1.0.0...1.1.0) (2023-12-23)


### Features

* add the uri mapping logic to the ecosystem management interface ([8428e3b](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/8428e3b008aca2878c08001c09375469a7047bc2))
* add websocket support to web client ([04ef20a](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/04ef20a8cce63e19862d37376c5141c4a0bb00d2))
* implement base version of the ecosystem registry ([9ea2703](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/9ea270381922bb0c7f1b1d184329b1176c6c3ae4))
* implement deletion of digital twin route in ecosystem management interface ([b0badca](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/b0badcaa47728f607ece7b08f307ad5462c57247))
* implement ecosystem management interface api ([9f7bf2b](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/9f7bf2b6aa18d6c83b13c957b5d46ff9925008e9))
* implement first version of the ecosystem management interface ([7982ba9](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/7982ba97645de98c7b0d32db72e486f21f5bd103))
* implement first version of the platform knowledge graph engine ([052b7f8](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/052b7f88d73e4440cb8a50de1ce3db29c2e162fd))
* implement wodt digital twins observer ([a6109aa](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/a6109aa803c728882792d657bb9b4b7d890b13a1))
* implement wodt digital twins platform interface api ([a88a378](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/a88a378f6abc206467663ad87e295b08c2fc2d0d))
* include platform knowledge graph in the engine ([54a6b8d](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/54a6b8d371aa732aea4f78d2b1efd744a10fa2f5))
* move first steps towards update of dtds ([fde2c36](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/fde2c364d05b70fd5f4dd18a15157a23696f5dab))


### Dependency updates

* **deps:** add archunit dependency ([51e40fe](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/51e40fe079c32739bce98d27cfdd2aaf25b68b71))
* **deps:** add jena dependency ([cf98fdd](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/cf98fddaa247b619d8d156c489e38f211862f307))
* **deps:** add kotlin coroutines dependency ([fc76e79](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/fc76e79ea5bddc135fefbb364d6ae7fcea2e2867))
* **deps:** add kotlin logging dependency ([ccf342c](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/ccf342ca439b13897263d62a8522b852d437f681))
* **deps:** add kotlin serialization dependency ([f9f6fff](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/f9f6fff9f47d4cbc4e6886fa2742b863e1916d59))
* **deps:** add ktor client dependencies ([c35d68c](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/c35d68c71ff98d5e3f92782e984e0e2413ae7c09))
* **deps:** add ktor client websocket plugin ([4ffb5d4](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/4ffb5d4dd1cacc3c56ac0331835173cba1fb00a5))
* **deps:** add ktor server dependencies ([4798ef7](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/4798ef719272b74dd7bec49cb93cf4cf4cd9e04b))
* **deps:** delete unused ktor dependency ([89aef9a](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/89aef9acc37627df4ac16cc11bfed215555903d7))
* **deps:** update jena version manually because i need new features ([7c5dbca](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/7c5dbca99956c0310e1b493abe081af5a1eaa26a))
* **deps:** update plugin kotlin-qa to v0.57.1 ([8c72801](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/8c72801b25cbeb354b02062773d50c87e4421287))


### Bug Fixes

* use synchronized set for concurrency ([a904549](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/a90454969a4204adbba1a1da7bc4dd18870208b5))


### Tests

* add ecosystem management interface api tests ([bf93d5d](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/bf93d5dcf169710c2e8432936c2792bd46e89ffa))
* add ecosystem registry and platform knowledge graph engine to ktor testing utility ([1fb5ede](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/1fb5edecb50e871090ece2362a2ea4ebee672cd4))
* add ktor testing utilities ([3ba51fb](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/3ba51fb87391cda340bc351f9d25ed8943d4e92a))
* add platform knowledge graph engine tests ([3478652](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/34786522e430c72a9e9a90a71fdbdad4c3bed459))
* add resources for tests ([170c5c6](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/170c5c6b643883142f9c1bc623be7a46d2d8da5c))
* add specific test on the presence of supported observation protocols ([5ad0e86](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/5ad0e864005ceb75be2fbf09d44c0050a6492f69))
* add test for clean architecture ([dc38b17](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/dc38b17a9ab2afe9c1ea7f469954bc6f3e4e3db1))
* add test resources for wot dtd tests ([bdf3664](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/bdf3664cabf9d7149a597147ef2262ad6f90c613))
* add testing utilities ([3e6e08e](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/3e6e08e3c135130aececf21cf0a79fddd6d330e7))
* add tests for deletion route in ecosystem management interface api ([f8864ad](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/f8864ad32a0741026d673dae12eaff56cfef603a))
* add tests for the ecosystem management interface ([b5d7dfc](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/b5d7dfcf98035bc6a78cf25b592a219316801d6b))
* add tests for the ecosystem management interface uri mapping logic ([4219a1a](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/4219a1a3044f21ddb31a98eb51ad2470970e3dab))
* add tests for the ecosystem registry ([39f46ca](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/39f46cacc42cc77bffea51991f0f6ff6e0cd4319))
* add tests for wodt digital twins platform interface api ([cc9b6f4](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/cc9b6f45e42cdf03e0742cad06793ee0d4fbc6b3))
* add tests for wot dtd ([0197caf](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/0197cafab453d724a3a13f4abb0efedb9cf59575))
* **refactor:** refactor test code ([29176e8](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/29176e8c4b33a3f7f7caa2c2587284c51990901f))


### Build and continuous integration

* set the main class ([a4e1f17](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/a4e1f17afdae60186add8c898ede2f2602b56d2a))


### General maintenance

* add and set the base engine ([5860bb6](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/5860bb6bb2b4ef9adc0fe76a4f272308e8645a3e))
* add digital twin descriptor model ([a25b091](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/a25b09110c43365335462bfbbff50b68b8c6c3b7))
* add digital twin uri value object ([947bb0f](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/947bb0f692c490889a3a008fe15f6c35c0fe55b3))
* add dtd deserialization ([cae6d7e](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/cae6d7ee1cdac10c31f9b89c466e7f91c1f03491))
* add ecosystem events model ([732c74c](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/732c74c600d563ae15a7ce0bfcf69ceb6fcf22dd))
* add ecosystem management interface component interface ([1b79c0c](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/1b79c0c9e2188f0371dfca032f44f633dc3ebde5))
* add exposed port to ecosystem management interface to be able to map uris ([fcb9ade](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/fcb9adecd9b893ce7aa19978b1c0f544f8c4bf02))
* add platform registration notification payload ([34519e7](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/34519e77c7cea11bf29c8457d10b1ed3b3045cf3))
* add real names of protocols and content types for dtd ([97cf116](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/97cf116a6cb736ba25bdeb4c2f876c05d92e37ef))
* add test resource for observation protocols check ([05ffba3](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/05ffba389b426b28c32c8b65afc2aea117db3725))
* add the ecosystem management interface component to the engine ([d0708d0](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/d0708d09f15f733b0db8f9cc82f7909547a92ee5))
* add wodt digital twin observer to the engine ([94ad5a5](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/94ad5a566c2e9dc725424ec9a87d8d2d48976a4c))
* add wodt digital twins platform interface to the platform web server ([038bd93](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/038bd93de5bbfb977a8406950f814602ae15212e))
* add wodt vocabulary ([42b6782](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/42b6782d720335aef4df935ad4fdbd26f5585d7e))
* create ecosystem registry concept ([baff5c6](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/baff5c6082eb01c1726629db94418458cbc9b9cc))
* create platform knowledge graph engine component concept ([ee4db35](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/ee4db3519ea6500f3b0f97bf6155afea5aecc7c6))
* create the concept of wodt platform http client ([34ceb30](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/34ceb3015662395b664672a57c926356b40394af))
* create wodt digital twins observer component concept ([f694be3](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/f694be34253b5f937174c7220e614cac33ea01c6))
* create wodt platform web server ([7bf5e2e](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/7bf5e2e152c743c0d4a7987b0d55575cca0f6bcb))
* create wodt platform web server concept ([6d3edfc](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/6d3edfc2edb671180bea136148d8c3c834ffcec0))
* delete template files ([6a4e8e9](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/6a4e8e9d85f2c699da3319e041627f4028e4e9c7))
* deny sparql update queries ([8e5fb8d](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/8e5fb8d16294964253a3e17cc8e7ff375579da05))
* implement the wodt platform http client ([d051c04](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/d051c04775972508a051153da3043391ec587869))
* improve wot dtd construction logic ([d409d16](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/d409d16489028c382f8e058c67714ec271d0adbc))
* include wodt platform web server in the engine ([929e0a1](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/929e0a13d9e3d53d51691dffcf039c046390eaf0))
* make registration suspendable ([d1f29a0](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/d1f29a0e1bc8a99026370bf77bdcba7fc03cab2a))
* model the dtd implementation with wot thing description ([033b859](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/033b8599f7c62e2ea61d823b51451234d6bf117f))
* remove useless logging in the wodt platform engine ([38c5f1e](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/38c5f1eccead65b16898a5e9470c0513d094db73))
* remove websocket when closed ([b9b4120](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/b9b412048a41dfb20edd551fdb06a8bc66db47f8))
* remove websocket when it closes unexpectedly ([670995b](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/670995bc7882a9da7ab05fc01f14534e1a19e831))
* return not mapped digital twin uris ([2809b3f](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/2809b3f6b0d235e40238fbf29589bdbcbd0096af))
* update README with environment variables ([4c245f6](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/4c245f62643d413eba56f1228a139434466ce5a4))
* use platform concepts where possible ([e35dffe](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/e35dffed3d36d1946a9c92bcad9cee4a680c6d8e))
* use the ecosystem registry ([752288f](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/752288f7c4758ffe15f1a0aa4bf1a734dcc67bfe))


### Style improvements

* follow detekt styleguide ([6577531](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/6577531aa4f56e6615259520366ece68cb7b8a3d))
* follow style guides ([f15495a](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/f15495aa5d607ea7e86b9bc29d782df16e3b47c1))
* improve name shadowing in lambdas ([98239aa](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/98239aab7a9d45b90d65334677daab73848e9176))


### Refactoring

* extract digital twin uri construction from path parameters ([671c3cf](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/671c3cfec3ab40cb1bee474d5114603664726fc0))
* move ecosystem management interface from infrastructure to application due to its non-infrastructural dependencies ([d9eeccd](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/d9eeccdbbeae9b915e630ed1d20ce0ca4af6a402))
* retrieve the cached version using the original digital twin uri ([b195710](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/b195710ac36d0de21a0f35c37b7c7af195831226))
* separate interfaces for web client following isp ([95cdb73](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/95cdb7326ff6af91b2154d20478003aaf60ca128))
* use wodt vocabulary in query instead of manually write the uri ([a099898](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/a099898fb50a680de885ed249bc60c03b0f715b2))

## 1.0.0 (2023-12-22)


### Dependency updates

* **core-deps:** update dependency org.jetbrains.kotlin:kotlin-stdlib to v1.9.22 ([0baff29](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/0baff29953458080f13e3717333e55df221a5dc0))


### Build and continuous integration

* add permissions to write to packages and gh pages ([babc37d](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/babc37d1abde969d9124561fefe1241560cbfcba))


### General maintenance

* delete unnecessary file ([0bb41bf](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/0bb41bf22c8165ffe9aec37e477346b4d507e096))
* **license:** update license ([0213915](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/0213915029ebaafd2f7909f3a4c2216c10c6ed18))
* update Dockerfile ([79d1b28](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/79d1b282d498ab8f9beced6a7866720cdef7e276))
* update project and group name ([487ac24](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/487ac24e6570f9edfd29749875b7917fdf7a22f5))
* update readme ([04515e4](https://github.com/WebBased-WoDT/wodt-dts-platform/commit/04515e43be2f9ac9f4e86076dbace3dec76afa9d))
