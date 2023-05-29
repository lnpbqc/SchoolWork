<template>
  <div id="chat">
    <ChatInfo></ChatInfo>
    <ChatList></ChatList>
    <ChatContainer></ChatContainer>
  </div>
</template>

<script>
import ChatList from "@/components/ChatList.vue";
import ChatContainer from "@/components/ChatContainer.vue";
import ChatInfo from "@/components/ChatInfo.vue";
export default {
  name: "ChatView",
  components:{
    ChatList,
    ChatContainer,
    ChatInfo
  },
  data(){
    return {
      name:"",
      userList: []
    }
  },
  mounted() {
    this.init()
  },
  methods:{
    init(){
      this.name = sessionStorage.getItem("userName")
      this.getUserList()
    },
    getUserList(){
      let data = new FormData()
      data.append("user",sessionStorage.getItem("userName"))
      data.append("key",sessionStorage.getItem("key"))
      this.$http.post("/api/users",data).then(resp=>{
        let users = resp.body
        if(users==="非法获取用户"){
          alert("请联系系统管理员")
          return
        }
        for(let i = 0;i<users.length;i++){
          users.online = users.online === "true"
        }
        this.userList = users
        this.$bus.$emit("watchList",users)
      },err=>{
        alert("请重新刷新登录")
      })
    }
  }
}
</script>

<style scoped lang="less">
#chat{
  width: 100vw;
  height: 100vh;
  overflow: hidden;
}
</style>