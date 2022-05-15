var boyakiApp = {
	data(){
		return{
			category:[],
			status:[],
			keyword:"",
			page:0,
			PostRecords:[],
			disabled:false
		}
	},
	methods: {
		resetSearch(){
			this.category = []
			this.status = []
			this.keyword = ""
			this.startSearch()
		},
		startSearch(){
			this.page = 0
			this.disabled = false
			this.PostRecords = []
			document.getElementById("scroll").value = '追加で読み込む'
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
				console.log(response.data.length)
				if(response.data.length == 0 && this.page == 0){
					this.disabled = true
					document.getElementById("scroll").value= '該当する投稿は見つかりませんでした'
				}else if(response.data.length == 0 && this.page != 0){
					this.disabled = true
					document.getElementById("scroll").value = 'これ以上の投稿はありません'
				}else {
					for(let i=0;i<response.data.length;i++){
						    this.PostRecords.push(response.data[i])
						}
					if(response.data.length <= 4){
						this.disabled = true
						document.getElementById("scroll").value = 'これ以上の投稿はありません'
					}
				}
				this.page += 1
			})
			.catch(error =>{
				console.log(error)
				this.disabled = true
				document.head.querySelector("scroll").textContent = 'サーバとの通信でエラーが発生しました'
			})
		}
	},
	mounted(){
              this.search()
	}
}
Vue.createApp(boyakiApp).mount('#boyakiApp');