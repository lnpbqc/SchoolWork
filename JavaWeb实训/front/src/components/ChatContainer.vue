<template>
  <div id="container">
    <div id="chatWith" v-show="chatname!==''">你正在和{{chatname}}聊天</div>
    <div id="chatSpace">
      <div v-for="(msg,index) of msgs" :key="index" class="chatBox">
        <div class="session" v-if="msg.from===name" style="float: right">
          <div class="userRight">{{msg.from}}</div>
          <div class="chatContentRight">
            <span v-if="msg.msg.type==='text'">{{msg.msg.content}}</span>
            <img v-else :src="'/api/getImage?type='+msg.msg.type+'&name='+msg.msg.content+'&user='+name" />
          </div>
        </div>
        <div class="session" v-else style="float:left;">
          <div class="userLeft">{{msg.from}}</div>
          <div class="chatContentLeft">
            <span v-if="msg.msg.type==='text'">{{msg.msg.content}}</span>
            <img v-else :src="'/api/getImage?type='+msg.msg.type+'&name='+msg.msg.content+'&user='+chatname" />
          </div>
        </div>
      </div>
    </div>
    <div class="chat-input">
      <textarea
          ref="input"
          v-model="message"
          placeholder="请输入消息"
          @keydown.prevent.shift.enter="previewMessage+='&&'"
          @keydown.exact.enter="sendMessage"
          @input="updatePreview"></textarea>
      <div class="preview">{{ previewMessage }}</div>
      <button class="send-btn" @click="sendMessage">发送</button>
    </div>
    <div class="ImgOrEmo">
      <label for="sendImg">发送图片</label>
      <input type="file" id="sendImg" @change="handleImageUpload">
      <button @click="chooseEmo">Emo</button>
    </div>
    <div id="Emos" v-show="selectEmo" @mouseleave="selectEmo=false">
      <div v-for="(emo,index) of emos" :key="index" class="emo">
        <img :src="emo" @dblclick="sendEmo(emo)">
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "ChatContainer",
  data() {
    return {
      viewCnt:2,
      message: '',
      previewMessage: '',
      chatname:"示例",
      name:"",
      msgs:[
        {
          from:"123",
          msg:{
            type:"text",
            content:"测试"
          }
        },
        {
          from:"示例",
          msg:{
            type:"emo",
            content:"100@2x.gif"
          }
        },
        {
          from:"示例",
          msg:{
            type:"text",
            content:"吃什么"
          }
        },
        {
          from:"示例",
          msg:{
            type:"emo",
            content:"106@2x.gif"
          }
        }
      ],
      emos:[],
      selectEmo:false
    }
  },
  created() {
    this.name = sessionStorage.getItem("userName")
    this.connect()
  },
  mounted() {
    this.$bus.$on("getChatName",this.changeName)
    this.$bus.$on("offline",this.offline)
    this.$bus.$on("online",this.online)
  },
  beforeDestroy() {
    this.$bus.$off("getChatName")
    this.$bus.$off("offline")
    this.$bus.$off("online")
  }
  ,
  methods: {
    connect(){
      let that = this
      this.socket = new WebSocket("ws://localhost:8888/chat/"+this.name)
      this.socket.onopen = ()=>{
        that.$bus.$emit("setStatus","true")
      }
      this.socket.onmessage=(e)=>{
        let data = e.data
        // message格式 from to msg
        let message = JSON.parse(data)
        if(message.from ==="system"){
          that.$bus.$emit("updateStatus",message.msg.toString().split(","))
        }else{
          //接收消息
          let newData
          if(this.chatname===message.from){
            that.msgs.push({
              from:message.from,
              msg:{
                type:message.msg.type,
                content:message.msg.content
              }
            })
            localStorage.setItem(sessionStorage.getItem("userName")+"-"+message.from,JSON.stringify(that.msgs))
          }else{
            //不在一个聊天框发信息
            let newMessage = {
              from:message.from,
              msg:{
                type:message.msg.type,
                content:message.msg.content
              }
            }
            let storeData = localStorage.getItem(sessionStorage.getItem("userName")+"-"+message.from)
            if(storeData==null){
              localStorage.setItem(sessionStorage.getItem("userName")+"-"+message.from,JSON.stringify([]))
              storeData = "[]"
            }
            newData = JSON.parse(storeData)
            newData.push(newMessage)
            // that.$bus.$emit("showNewMessageInfo",newMessage)
            that.$bus.$emit("showNewMessageInfo",newMessage)
            localStorage.setItem(sessionStorage.getItem("userName")+"-"+message.from,JSON.stringify(newData))
          }
        }
        that.updateView()
      }
      this.socket.onclose = ()=>{
        // 关闭链接
        that.$bus.$emit("setStatus",false)
      }
      this.socket.onerror = ()=>{
        // 出错
        console.log("出错时间："+new Date().getTime())
      }
    },
    online() {
      let user = sessionStorage.getItem("userName")
      let key = sessionStorage.getItem("key")
      let data = new FormData()
      data.append("user",user)
      data.append("key",key)
      this.$http.post("/api/reLogin",data).then(resp=>{
        if(resp.body.msg==="123"){
          alert("不得重复登录，或联系管理员更改密码")
        }else if(resp.body.msg==="n"){
          alert("非法登录")
        }else{
          this.connect()
        }
      },err=>{
        console.log("出错时间："+new Date().getTime()+"--->"+JSON.stringify(err))
      })
    },
    offline() {
      this.socket.close()
    },
    chooseEmo(){
      this.selectEmo = true
      console.log("getting emos from server")
      if(this.emos.length!=0)return
      this.$http.get("/api/getAllEmos").then(resp=>{
        if(this.emos.length>0)return
        this.emos = resp.body
      },err=>{
        console.log(err)
      })
    },
    updateView(){
      this.$nextTick(()=>{
        setTimeout(()=>{
          const chatroom = document.querySelector("#chatSpace")
          chatroom.scrollTop = chatroom.scrollHeight
          this.$refs.input.focus()
        },400)
      })
    },
    changeName(name){
      //换一个消息，并获取存在本地的消息记录
      this.chatname = name
      this.msgs = JSON.parse(localStorage.getItem(sessionStorage.getItem("userName")+"-"+this.chatname))
      if(this.msgs===null) {
        this.msgs = []
        localStorage.setItem(sessionStorage.getItem("userName")+"-"+this.chatname,JSON.stringify(this.msgs))
      }
      this.updateView()
    },
    sendMessage() {
      if(this.chatname.trim()===""||this.chatname.trim()==="示例"||this.previewMessage.trim()==="")return
      let data = {
        from:sessionStorage.getItem("userName"),
        to:this.chatname
      }

      let msg = {
        type:"text",
        content:this.previewMessage
      }

      data.msg = msg
      this.socket.send(JSON.stringify(data))

      // {
      //   from:"cbn",
      //   msg:{
      //       type:"text",
      //       content:"吃什么"
      //   }
      // }
      this.msgs.push({
        from:this.name,
        msg
      })

      localStorage.setItem(sessionStorage.getItem("userName")+"-"+this.chatname,JSON.stringify(this.msgs))
      this.updateView()
      this.message = ''
      this.previewMessage = ''
      this.$refs.input.focus()
    },
    handleImageUpload(e){
      const file = e.target.files[0]
      let data = new FormData()
      data.append("user",sessionStorage.getItem("userName"))
      data.append("key",sessionStorage.getItem("key"))
      data.append("file",file)
      this.$http.post("/api/uploadImage",data).then(resp=>{
        if (resp.body===""){
          alert("服务器接收图片失败")
        }else{
          let send = {
            from:this.name,
            to:this.chatname,
            msg:{
              type:"img",
              content:file.name
            }
          }
          this.socket.send(JSON.stringify(send))
          delete send.to
          this.msgs.push(send)
          localStorage.setItem(sessionStorage.getItem("userName")+"-"+this.chatname,JSON.stringify(this.msgs))
        }
      },err=>{
        console.log(err)
      })
      this.updateView()
    },
    sendEmo(emoSrc){
      this.selectEmo = false
      let send = {
        from:this.name,
        to:this.chatname,
        msg:{
          type:"emo",
          content:emoSrc.split("=")[2].split("&")[0]
        }
      }
      this.socket.send(JSON.stringify(send))
      delete send.to
      this.msgs.push(send)
      localStorage.setItem(sessionStorage.getItem("userName")+"-"+this.chatname,JSON.stringify(this.msgs))
      this.updateView()
    },
    updatePreview() {
      let msg = this.message
      msg.replace("&&","\n")
      // let regex = [
      //   /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/,
      //   /^1[3-9]\d{9}$/,
      //   /^([1-9]\d{5}(18|19|20)\d{2}(0\d|1[0-2])([0-2]\d|3[0-1])\d{3}[0-9Xx])|([1-9]\d{5}\d{2}(0\d|1[0-2])([0-2]\d|3[0-1])\d{3})$/,
      //   /^[1-9]\d{5}$/,
      //   /^(https?|ftp):\/\/[^\s/$.?#].[^\s]*$/i
      // ]
      // for(let i = 0;i<regex.length;i++){
      //   msg = msg.replace(regex[i], "[保护ing]")
      // }
      this.previewMessage = msg
    }
  }
}
</script>

<style scoped lang="less">
#container{
  position: absolute;
  right: 5vw;
  bottom:5vh;
  width: 70vw;
  height: 80vh;
  border-radius: 10px;
  box-shadow: 0 0 10px 1px gray inset;
  overflow: hidden;
  #chatWith{
    letter-spacing: 3px;
  }
  #chatSpace{
    position: absolute;
    top: 30px;
    left: 0;
    height: 60vh;
    width: 100%;
    overflow-y: auto;
    overflow-x: hidden;
    .chatBox{
      width: 100%;
      min-height: 10vh;
      position: relative;
      overflow: hidden;
      margin: 1rem 0;
      .session{
        max-width: 50%;
        text-align: left;
        .userRight{
          position: absolute;
          right: 4px;
          top: 4px;
          width: 30px;
        }
        .userLeft{
          position: absolute;
          left: 4px;
          top: 4px;
          width: 30px;
        }
        .chatContentLeft{
          padding: 10px 20px;
          border: 1px solid black;
          border-radius: 20px;
          position: relative;
          left: 40px;
        }
        .chatContentRight{
          padding: 10px 20px;
          border: 1px solid black;
          border-radius: 20px;
          position: relative;
          right: 40px;
        }
      }
    }
  }
  .chat-input {
    display: block;
    position: absolute;
    width: 100%;
    height: 10vh;
    line-height: 10vh;
    box-shadow: 0 0 10px 1px gray inset;
    bottom: 0;
    left: 0;
    textarea {
      display: block;
      position: absolute;
      top: 50%;
      transform: translate(10px,-50%);
      width: 70%;
      height: 6vh;
      resize: none;
      border: none;
      outline: none;
      font-size: 16px;
      padding: 10px;
      margin-right: 10px;
      border-radius: 4px;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
      &:focus{
        border: 1px solid black;
      }
    }
    .preview {
      font-size: 14px;
      margin-top: 5px;
      color: #999;
      position: fixed;
      left: 25vw;
      bottom: 15vh;
    }
    .send-btn {
      background-color: #fff;
      color: #000;
      border: none;
      outline: none;
      border-radius: 4px;
      padding: 10px 20px;
      cursor: pointer;
      font-size: 16px;
      position: absolute;
      right: 10px;
      bottom: 50%;
      transform: translateY(50%);
    }
  }
  .ImgOrEmo{
    position: absolute;
    right: 95px;
    bottom: 15px;
    label,button{
      display: block;
      float: left;
      background-color: #fff;
      color: #000;
      border: none;
      line-height: 40px;
      border-radius: 4px;
      cursor: pointer;
      width: 90px;
      height: 40px;
      border: 1px solid black;
    }
    input{
      display: none;
      position: absolute;
      bottom: -100px;
    }
  }
  #Emos{
    width: 300px;
    height: 120px;
    position: absolute;
    right: 20px;
    bottom: 80px;
    overflow-y: auto;
    background-color: white;
    .emo{
      width: 30px;
      height: 30px;
      float: left;
      &:hover{
        filter: drop-shadow(0px 0px 4px gray);
      }
    }
  }
}
</style>