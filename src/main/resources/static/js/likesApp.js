function check(){
    if (confirm("削除してもよろしいですか？")) {
        return true;
    } else {
        return false;
    }
}
var likesApp = {
methods:{ 
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
	}
}
Vue.createApp(likesApp).mount('#likesApp');