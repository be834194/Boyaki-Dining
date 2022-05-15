var profileApp = {
	data(){
		return{
			page:0,
			PostRecords:[],
			disabled:false
		}
	},
	methods: {
		find(){
			var nickName = document.getElementById('nickName')
			axios.get('/api/find',{params:{
				                             nickName:nickName.textContent,
				                             page: this.page
									        }
									 })
			.then(response => {
				if(response.data.length == 0 && this.page == 0){
					this.disabled = true;
					document.getElementById("scroll").value = 'まだ投稿がありません'
				}else if(response.data.length ==　0 && this.page != 0){
					this.disabled = true;
					document.getElementById("scroll").value = 'これ以上の投稿はありません'
				}else{
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
				document.head.querySelector("scroll").value = 'サーバとの通信でエラーが発生しました'
			})
		}
	},
	mounted(){
		this.find()
	}
}
Vue.createApp(profileApp).mount('#profileApp');