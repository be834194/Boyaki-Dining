const regex = /^[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$/; //yyyy-mm-ddの正規表現
var query = location.search;
var value = query.split('=');
var dateValue = decodeURIComponent(value[1]);
var getId = document.getElementById('categoryId').value;

var category = {
	data(){
		return{
			vueId:getId,
			date:dateValue,
			validResult:true
		}
	},
	methods:{
		check() {
			var today = new Date();
			today.setDate(today.getDate());
			var yyyy = today.getFullYear();
			var mm = ("0"+(today.getMonth()+1)).slice(-2);
			var dd = ("0"+today.getDate()).slice(-2);
			if(dateValue == null) { // クエリの値が無いなら、今日の日付
			    document.getElementById("diaryDay").value = yyyy+'-'+mm+'-'+dd;
			}else if(regex.test(this.date)){ // 正規表現が正しいなら、取得した日付
				document.getElementById("diaryDay").value = this.date;
		    }else { // 正規表現が間違いなら、今日の日付
			    document.getElementById("diaryDay").value = yyyy+'-'+mm+'-'+dd;
		    }
        },
		uploads(event) {
			let file = event.target.files[0],
			size = file.size
			type = file.type
            errors = ''
		    if (size > 10485760) {
		      errors += 'ファイルの上限サイズ10MBを超えています\n'
		    }
		    if(type != 'image/jpeg'){
			  errors += 'jpg,jpegファイルのみアップロード可能です'
			}
		    if (errors) {//errorsが存在する場合は内容をalert
		      alert(errors)
		      event.currentTarget.value = null
		    }
         }
	},
	mounted(){
		this.check();
	}
};
Vue.createApp(category).mount('#category'); 