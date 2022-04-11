var boyakiApp = {
	data(){
		return{
			category:[],
			status:[],
			keyword:"",
			page:0,
			PostRecords:[],
			observe_element:null,
			observer:null
		}
	},
	methods: {
		resetAll(){
			this.category = []
			this.status = []
			this.keyword = ""
		},
		startSearch(){
			this.page = 0
			this.PostRecords = []
			this.observer.observe(this.observe_element)
			document.getElementById("scroll").innerText = 'loading...'
			this.search()
		},
		search(){
			var tokenValue = document.head.querySelector("[name=_csrf]").content;
			let params = new FormData()
            params.append('category', this.category)
            params.append('status',this.status)
            params.append('keyword',this.keyword)
            params.append('page',this.page)
            params.append('_csrf',tokenValue)
            
			axios.post('/api/search',params,{timeout: 5000})
			.then(response => {
				if(response.data.length == 0 && this.page == 0){
					document.getElementById("scroll").innerText = '該当する投稿は見つかりませんでした'
					this.observer.unobserve(this.observe_element)
				} else if(response.data.length == 0 && this.page != 0){
					document.getElementById("scroll").innerText = 'これ以上の投稿ははありません'
					this.observer.unobserve(this.observe_element)
				}
				 else{
					for(let i=0;i<response.data.length;i++){
					    this.PostRecords.push(response.data[i])
					}
				  this.page += 1
				}
			})
			.catch(error =>{
				console.log(error)
				document.head.querySelector("scroll").textContent = 'サーバとの通信でエラーが発生しました'
			})
		}
	},
	mounted(){
		this.observer = new IntersectionObserver((entries) => { //targetが画面に入るとcallback関数が実行される
            const entry = entries[0];  //監視対象の要素が配列で入っている
            if (entry && entry.isIntersecting) { //isIntersectingがtrue:ブラウザに入っている
              this.search()
            }
          });
        this.observe_element = this.$refs.observe_element
        this.observer.observe(this.observe_element)
	}
}
Vue.createApp(boyakiApp).mount('#boyakiApp');