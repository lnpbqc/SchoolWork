<template>
  <div class="user-list">
    <div class="user-item" v-for="(user,index) in list" :key="index" @click="chooseChat(user.name)" v-if="user.name!==name">
      <div class="user-info">
        <div class="user-name">{{ user.name }}</div>
        <div class="user-status">
          <span v-if="user.online=='true'" class="online">Online</span>
          <span v-else class="offline">Offline</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "ChatList",
  data(){
    return {
      list:[]
    }
  },
  mounted() {
    this.$bus.$on("watchList",this.changeList)
    this.$bus.$on("updateStatus",this.updateStatus)
  },
  beforeDestroy() {
    this.$bus.$off("watchList")
    this.$bus.$off("updateStatus")
  },
  computed:{
    name(){
      return sessionStorage.getItem("userName")
    }
  },
  methods:{
    chooseChat(name){
      this.$bus.$emit("getChatName",name)
    },
    changeList(list){
      this.list = list
    },
    updateStatus(names){
      for (let i = 0;i<this.list.length;i++){
        for(let j = 0;j<names.length;j++){
          if(names[j]===this.list[i].name){
            this.list[i].online = "true"
            break
          }else{
            this.list[i].online = "false"
          }
        }
      }
    }
  }
}
</script>

<style scoped lang="less">
.user-list{
  position: absolute;
  left: 4vw;
  top:15vh;
  width: 14vw;
  height: 80vh;
  box-shadow: 0px 0px 10px 1px gray;
  border-radius: 20px;
  //background-color: brown;
  overflow-y: auto;
  .user-item {
    margin: 30px;
    height: 3rem;
    box-shadow: 0 0 10px gray;
    border-radius: 10px;
    transition: all .5s;
    position: relative;
    overflow: hidden;
    &:hover{
      box-shadow: 0 0 12px black;
      cursor: pointer;
    }
    .user-name {
      font-weight: bold;
      margin-bottom: 5px;
      position: absolute;
      left: 10px;
      top: 0px;
      &:first-letter{
        font-size: 30px;
        top: -20px;
      }
    }
    .user-status {
      font-size: 6px;
      position: absolute;
      right: 1rem;
      top: 50%;
      transform: translateY(-50%);
      .online {
        color: green;
      }
      .offline {
        color: red;
      }
    }
  }
}
</style>