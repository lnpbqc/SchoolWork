<template>
  <div id="app">
    <router-view/>
  </div>
</template>

<style lang="less">
*{
  margin: 0;
  padding: 0;
}
/* 定义滚动条的样式 */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
  background-color: #f5f5f5;
}
/* 定义滑块的样式 */
::-webkit-scrollbar-thumb {
  background-color: #888;
  border-radius: 10px;
}
/* 定义滑块的最小高度和宽度 */
::-webkit-scrollbar-thumb:hover {
  background-color: #555;
}
/* 定义鼠标悬停在滑块上时的样式 */
::-webkit-scrollbar-thumb:active {
  background-color: #333;
}
/* 定义滑块在滚动条上的最小高度和宽度 */
::-webkit-scrollbar-thumb:vertical {
  min-height: 20px;
}
::-webkit-scrollbar-thumb:horizontal {
  min-width: 20px;
}
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  //background: url("@/assets/logo.png") no-repeat center;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
}
</style>
