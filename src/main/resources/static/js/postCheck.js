function postConfirm(){
    if (confirm("投稿内容に不適切な内容がないか確認してください")) {
        return true;
    } else {
        return false;
    }
}