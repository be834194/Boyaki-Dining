function check(){
    if (confirm("削除してもよろしいですか？")) {
        return true;
    } else {
        return false;
    }
}
var likesApp = {
	data(){
		return{
			page:0,
			CommentRecords:[],
			observe_element:null,
			observer:null
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
					document.getElementById("scroll").innerText = '該当するコメントは見つかりませんでした'
					this.observer.unobserve(this.observe_element)
				} else if(response.data.length == 0 && this.page != 0){
					document.getElementById("scroll").innerText = 'これ以上のコメントははありません'
					this.observer.unobserve(this.observe_element)
				}
				 else{
					for(let i=0;i<response.data.length;i++){
					    this.CommentRecords.push(response.data[i])
					}
				  this.page += 1
				}
			})
			.catch(error =>{
				console.log(error)
				document.head.querySelector("scroll").textContent = 'サーバとの通信でエラーが発生しました'
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
Vue.createApp(likesApp).mount('#likesApp');