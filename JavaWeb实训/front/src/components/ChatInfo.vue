<template>
  <div id="info">
    <div class="tool-box">
      {{date}}
    </div>
    <div class="user-status" @click.self="showChangePassword">
      {{name}}
      <span v-if="onlineStatus" class="online" @click="offline">Online</span>
      <span v-else class="offline" @click="online">Offline</span>
    </div>
    <div id="changePasswordBox" ref="changePasswordBox">
      <span style="position: absolute;right: 2vw;bottom: 0px;cursor: pointer;" @click="$refs.changePasswordBox.style.display = 'none',password1 = '',password2 = ''">取消</span>
      <input type="password" v-model="password1" placeholder="请输入新密码" style="--i:20px">
      <input type="password" v-model="password2" placeholder="请再次输入新密码" style="--i:40px">
      <button @click="changePassword">确定修改</button>
    </div>
    <div id="newMessageInfo" ref="newMessageInfoBox" @dblclick="$bus.$emit('getChatName',newMessageFrom)">
      在线用户：{{newMessageFrom}}给你发了：{{newMessageContent}}
    </div>
  </div>
</template>

<script>
export default {
  name: "ChatInfo",
  data(){
    return {
      name:"",
      onlineStatus:true,
      date:"",
      interval:undefined,
      password1:"",
      password2:"",
      newMessageFrom:"",
      newMessageContent:""
    }
  },
  mounted() {
    this.$bus.$on("setStatus",this.setStatus)
    this.$bus.$on("showNewMessageInfo",this.showNewMessageInfo)
    this.name = sessionStorage.getItem("userName")
    let date = new Date()
    this.date = date.getFullYear()+"年"+(date.getMonth()+1)+"月"+date.getDate()+"日\t"+date.getHours()+":"+date.getMinutes()
    this.interval = setInterval(()=>{
      let date = new Date()
      this.date = date.getFullYear()+"年"+(date.getMonth()+1)+
          "月"+date.getDate()+"日\t"+date.getHours()+":"+
          date.getMinutes()+":"+date.getSeconds()
    },1000)
  },
  beforeDestroy() {
    this.$bus.$off("setStatus")
    this.$bus.$off("showNewMessageInfo")
    clearInterval(this.interval)
  }
  ,
  methods:{
    setStatus(val){
      this.onlineStatus = val
    },
    online(){
      this.$bus.$emit("online")
    },
    offline(){
      this.$bus.$emit("offline")
    },
    showChangePassword(){
      this.$refs.changePasswordBox.style.display = "block"
    },
    changePassword(){
      if(this.password1.trim()===""||this.password2.trim()==="")return
      if (this.password2!==this.password1){
        alert("两次密码不一致")
        return
      }
      let user = sessionStorage.getItem("userName")
      let key = sessionStorage.getItem("key")
      let pwd = this.password1
      let data = new FormData()
      data.append("user",user)
      data.append("key",key)
      data.append("password",pwd)
      this.$http.post("/api/changePassword",data).then(resp=>{
        if(resp.body.msg=="y")alert("修改成功")
        else alert("修改失败")
      },err=>{
        console.log(new Date().getTime()+"发生了"+err)
      })
      this.password1 = ""
      this.password2 = ""
      this.$refs.changePasswordBox.style.display = "none"
    },
    showNewMessageInfo(message){
      if (message.msg.type==="img"){
        message.msg.content = "图片"
      }else if (message.msg.type ==="emo"){
        message.msg.content = "表情"
      }
      this.newMessageFrom = message.from
      this.newMessageContent = message.msg.content
      this.$refs.newMessageInfoBox.style.display = "block"
      let audio = new Audio(require('@/assets/tim.wav'));
      audio.play();
      setTimeout(()=>{
        this.$refs.newMessageInfoBox.style.display = "none"
        this.newMessageFrom = ""
        this.newMessageContent = ""
      },5000)
    }
  }
}
</script>

<style scoped lang="less">
#info{
  height: 10vh;
  width: 100vw;
  line-height: 10vh;
  border-bottom: 1px solid gray;
  .tool-box {
    float: left;
    width: 20vw;
    height: 100%;
    position: relative;
    filter: drop-shadow(0px 0px 10px lightskyblue);
  }
  .user-status {
    width: 40vw;
    height: 100%;
    float: right;
    font-size: 30px;
    &:hover{
      cursor: pointer;
    }
    .online,.offline{
      font-size: 10px;
    }
    .online {
      color: green;
    }
    .offline {
      color: red;
    }
  }
  #changePasswordBox{
    position: absolute;
    display: none;
    height: 25vh;
    width: 20vw;
    top: 10vh;
    right: 10vw;
    z-index: 1;
    border: 1px solid black;
    border-radius: 1vw;
    background-color: whitesmoke;
    transition: all 2s;
    input{
      display: block;
      width: 80%;
      height: 20px;
      padding: 2px 10px;
      margin-top: var(--i);
      margin-left: 1vw;
    }
  }
  #newMessageInfo{
    display: none;
    position: absolute;
    top: 2vh;
    left: 50%;
    width: 30vw;
    height: 6vh;
    line-height: 6vh;
    transform: translate(-50%,0);
    border: 1px solid gray;
    border-radius: 3vh;
    cursor: pointer;
    user-select: none;
  }
}
</style>