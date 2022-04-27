var getId = document.getElementById('categoryId').value;
var category = {
	data(){
		return{
			vueId:getId
		}
	},
	methods: {
		uploads(event) {
			let file = event.target.files[0],
			size = file.size
            errors = ''
		    //上限サイズは10MB
		    if (size > 10000000) {
		      errors += 'ファイルの上限サイズ10MBを超えています'
		    }
		    if (errors) {
		      //errorsが存在する場合は内容をalert
		      alert(errors)
		      //valueを空にしてリセットする
		      event.currentTarget.value = ''
		    }
         }
    }
};
Vue.createApp(category).mount('#category'); 