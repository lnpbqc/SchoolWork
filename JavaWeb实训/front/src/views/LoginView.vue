<template>
  <div>
    <div v-show="MsgFromSystem" id="msg">{{MsgFromSystem}}</div>
    <div id="container">
      <div id="login" ref="login">
        <img :src="require('@/assets/logo.png')">
        <input type="text" v-model="name" placeholder="账号">
        <input type="password" v-model="password" placeholder="密码">
        <button @click="login">登录</button>
      </div>
      <div id="register" ref="register">
        <input type="text" v-model="name" placeholder="请输入要注册的账号" style="--i:10%">
        <input type="password" v-model="password1" placeholder="请输入密码" style="--i:30%">
        <input type="password" v-model="password2" placeholder="请再次输入密码" style="--i:50%">
        <button @click="register">注册</button>
      </div>
      <span @click="change">跳到：{{msg}}</span>
    </div>
  </div>
</template>

<script>
export default {
  name: "LoginView",
  data(){
    return {
      name:"",
      password:"",
      password1:"",
      password2:"",
      type:true,
      msg:"注册》",
      MsgFromSystem:undefined
    }
  },
  methods:{
    change(){
      if(this.type){
        this.$refs.login.style.transform = "translateX(-100%)"
        this.$refs.register.style.transform = "translateX(-100%)"
        this.msg = "《登录"
      }else{
        this.$refs.register.style.transform = "translateX(0%)"
        this.$refs.login.style.transform = "translateX(0%)"
        this.msg="注册》"
      }
      this.type = !this.type
    },
    login(){

      if (this.name.trim()===""){
        this.MsgFromSystem = "账号为空"
        return
      }
      if(this.password.trim()===""){
        this.MsgFromSystem = "密码为空"
        return
      }

      let data = new FormData()
      data.append("account",this.name)
      data.append("password",this.password)

      this.$http.post('/api/login',data).then(resp => {
        // console.log(resp.body)
        if(resp.body.msg==="123"){
          this.MsgFromSystem = "不得重复登录，或联系管理员更改密码"
        }else if(resp.body.msg==="n"){
          this.MsgFromSystem = "登录出错，检查账号密码"
        }else{
          this.MsgFromSystem = "登录成功"

          sessionStorage.setItem("userName",this.name)
          sessionStorage.setItem("key",resp.body.msg)

          setTimeout(()=>{
            sessionStorage.setItem(this.name+"login",new Date().getTime().toString())
            this.$router.push("/index")
          },1000)
        }
      }, error => {
        console.log(error);
        this.MsgFromSystem ="登录失败，请联系管理员"
      });

    },
    register(){
      if (this.name.trim()===""){
        this.MsgFromSystem = "账号为空"
        return
      }
      if(this.password1.trim()===""||this.password2.trim()===""){
        this.MsgFromSystem = "密码为空"
        return
      }

      if(this.password1.trim()!==this.password2.trim()){
        this.MsgFromSystem = "密码不一致"
        return
      }

      let data = new FormData()
      data.append("account",this.name)
      data.append("password",this.password1)
      this.$http.post("/api/register",data).then(resp=>{
        if(resp.body.msg!=="n"){
          this.MsgFromSystem = "注册成功"
        }
      },err=>{
        console.log(err)
      })
    }
  }
}
</script>

<style scoped lang="less">
input{
  transition: all .5s;
  outline: none;
  border: 1px solid gray;
  padding-left: 1rem;
}
div{
  width: 100vw;
  height: 100vh;
  transition: all 2s;
  #msg{
    position: fixed;
    top: 20px;
    left: 50%;
    transform: translateX(-50%);
    color: red;
    font-weight: bold;
  }
  #container{
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%,-50%);
    width: 30%;
    height: 50%;
    min-height: 400px;
    min-width: 300px;
    border: 1px solid gray;
    border-radius: 20px;
    box-shadow: 0 0 6px gray;
    overflow: hidden;
    span{
      position: absolute;
      left: 50px;
      bottom: 30px;
      padding: 20px;
      font-size: 10px;
      cursor: pointer;
    }
    #login{
      width: 100%;
      height: 100%;
      position: absolute;
      left: 0;
      top: 0;
      img{
        display: block;
        height: 100px;
        width: 100px;
        border-radius: 50px;
        border: 1px solid gray;
        position: absolute;
        left: 50%;
        top: 10%;
        transform: translateX(-50%);
      }
      input{
        display: block;
        height: 30px;
        line-height: 30px;
        width: 200px;
        position: absolute;
        left: 50%;
        transform: translateX(-50%);
        &:focus{
          box-shadow: 0 0 10px gray;
        }
      }
      input[type="text"]{
        top: 40%;
      }
      input[type="password"]{
        top: 60%;
      }
      button{
        position: absolute;
        left: 90%;
        top: 70%;
        transform: translateX(-50%);
        padding: 10px;
        outline: none;
        border: 1px solid gray;
        background-color: white;
        cursor: pointer;
        border-radius: 30px;
      }
    }
    #register{
      width: 100%;
      height: 100%;
      position: absolute;
      left: 100%;
      top: 0;
      input{
        display: block;
        height: 30px;
        line-height: 30px;
        width: 200px;
        position: absolute;
        left: 50%;
        transform: translateX(-50%);
        top: var(--i);
        &:focus{
          box-shadow: 0 0 10px gray;
        }
      }
      button{
        position: absolute;
        left: 90%;
        top: 70%;
        transform: translateX(-50%);
        padding: 10px;
        outline: none;
        border: 1px solid gray;
        background-color: white;
        cursor: pointer;
        border-radius: 30px;
      }
    }
  }
}
</style>