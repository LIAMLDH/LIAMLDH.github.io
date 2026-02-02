<template>
  <div class="novel-reader">
    <div class="novel-header">
      <h1>{{ novelTitle }}</h1>
      <div class="controls">
        <button @click="toggleTheme" class="theme-btn">
          {{ isDarkMode ? '切换亮色' : '切换暗色' }}
        </button>
        <button @click="scrollToTop" class="scroll-btn">回到顶部</button>
      </div>
    </div>
    <div class="novel-content" ref="novelContent">
      <div v-if="novelText" class="novel-text">
        <p v-for="(paragraph, index) in novelParagraphs" :key="index" class="paragraph">
          {{ paragraph }}
        </p>
      </div>
      <div v-else class="loading">加载中...</div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'NovelReader',
  data() {
    return {
      novelTitle: '小说阅读',
      novelText: '',
      novelParagraphs: [],
      isDarkMode: false
    };
  },
  mounted() {
    this.loadNovel();
    this.checkTheme();
  },
  methods: {
    loadNovel() {
      fetch('/novel.txt')
        .then(response => response.text())
        .then(text => {
          this.novelText = text;
          this.novelParagraphs = text.split('\n').filter(p => p.trim());
        })
        .catch(error => {
          console.error('加载小说失败:', error);
          // 尝试直接加载public目录的文件
          fetch('/novel.txt')
            .then(response => response.text())
            .then(text => {
              this.novelText = text;
              this.novelParagraphs = text.split('\n').filter(p => p.trim());
            });
        });
    },
    toggleTheme() {
      this.isDarkMode = !this.isDarkMode;
      document.body.classList.toggle('dark-mode', this.isDarkMode);
    },
    checkTheme() {
      if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
        this.isDarkMode = true;
        document.body.classList.add('dark-mode');
      }
    },
    scrollToTop() {
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }
};
</script>

<style scoped>
.novel-reader {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  font-family: 'Georgia', serif;
}

.novel-header {
  text-align: center;
  margin-bottom: 30px;
  padding: 20px;
  border-bottom: 1px solid #eee;
}

.novel-header h1 {
  color: #333;
  font-size: 24px;
  margin-bottom: 15px;
}

.controls {
  display: flex;
  gap: 10px;
  justify-content: center;
}

.theme-btn,
.scroll-btn {
  padding: 8px 16px;
  border: 1px solid #ddd;
  background: #fff;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.theme-btn:hover,
.scroll-btn:hover {
  background: #f0f0f0;
}

.novel-content {
  min-height: 600px;
  line-height: 1.8;
  color: #333;
}

.paragraph {
  margin-bottom: 15px;
  text-indent: 2em;
}

/* 暗色模式 */
.dark-mode .novel-reader {
  background: #1a1a1a;
  color: #e0e0e0;
}

.dark-mode .novel-header h1 {
  color: #e0e0e0;
}

.dark-mode .paragraph {
  color: #d0d0d0;
}

/* 滚动条样式 */
.novel-content {
  max-height: 80vh;
  overflow-y: auto;
  padding-right: 10px;
}

.novel-content::-webkit-scrollbar {
  width: 8px;
}

.novel-content::-webkit-scrollbar-track {
  background: #f1f1f1;
}

.novel-content::-webkit-scrollbar-thumb {
  background: #888;
  border-radius: 4px;
}

.novel-content::-webkit-scrollbar-thumb:hover {
  background: #555;
}

.dark-mode .novel-content::-webkit-scrollbar-track {
  background: #333;
}

.dark-mode .novel-content::-webkit-scrollbar-thumb {
  background: #666;
}
</style>