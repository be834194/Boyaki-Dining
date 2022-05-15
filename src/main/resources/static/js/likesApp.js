var likesApp = {
	data(){
		return{
			page:0,
			CommentRecords:[],
			disabled:false
		}
	},
	methods:{ 
		search(){
			var postId = document.getElementById("postId").value
			var tokenValue = document.head.querySelector("[name=_csrf]").content;
			let params = new FormData()
			params.append('postId',postId)
            params.append('page',this.page)
            params.append('_csrf',tokenValue)
            
			axios.post('/api/comments',params,{timeout: 5000})
			.then(response => {
				if(response.data.length == 0 && this.page == 0){
					this.disabled = true
					document.getElementById("scroll").value = 'まだコメントはついていません'
				} else if(response.data.length == 0 && this.page != 0){
					this.disabled = true
					document.getElementById("scroll").value = 'これ以上のコメントはありません'
				}
				 else{
					for(let i=0;i<response.data.length;i++){
					    this.CommentRecords.push(response.data[i])
					}
					if(response.data.length <= 4){
						this.disabled = true
						document.getElementById("scroll").value = 'これ以上のコメントはありません'
					}
				  this.page += 1
				}
			})
			.catch(error =>{
				console.log(error)
				document.head.querySelector("scroll").value = 'サーバとの通信でエラーが発生しました'
			})
		},
		likes(){
				var tokenValue = document.head.querySelector("[name=_csrf]").content;
				var getId = document.getElementById("postId").value;
				let param = new FormData()
	            param.append('postId', getId)
	            param.append('_csrf',tokenValue)
	            axios.post('/index/boyaki/rate',param, {responseType : 'document',timeout: 5000})
					.then(response =>{
						const sumRate = document.getElementById('sumRate');
					    Array.from(response.data.body.childNodes).forEach((node) => {
					    sumRate.replaceWith(node)
					    });
					})
					.catch(error =>{
						console.log(error)
					})
		}
	},
	mounted(){
		this.search()
	}
}
Vue.createApp(likesApp).mount('#likesApp');