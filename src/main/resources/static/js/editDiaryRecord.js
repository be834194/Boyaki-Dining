var getId = document.getElementById('categoryId').value;
var category = {
	data(){
		return{
			vueId:getId,
			disabled:false
		}
	},
	methods:{
		doDisabled(){
			this.disabled = true
		},
		uploads(event) {
			let file = event.target.files[0],
			size = file.size
            errors = ''
		    if (size > 10000000) {
		      errors += 'ファイルの上限サイズ10MBを超えています'
		    }
		    if (errors) {//errorsが存在する場合は内容をalert
		      alert(errors)
		      event.currentTarget.value = null
		    }
         }
	}
};
Vue.createApp(category).mount('#category'); 