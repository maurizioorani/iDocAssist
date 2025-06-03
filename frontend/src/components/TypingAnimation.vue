<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue';
// import { motion } from 'motion'; // Assuming motion is installed and can be imported like this
import { cn } from '@/lib/utils';

const props = defineProps({
  text: {
    type: [String, Array],
    required: true,
  },
  speed: {
    type: Number,
    default: 100,
  },
  loop: {
    type: Boolean,
    default: false,
  },
  className: {
    type: String,
    default: '',
  },
});

const displayedText = ref('');
const currentTextIndex = ref(0);
let typingInterval = null;

const typeText = (textToType) => {
  let i = 0;
  displayedText.value = '';
  typingInterval = setInterval(() => {
    if (i < textToType.length) {
      displayedText.value += textToType.charAt(i);
      i++;
    } else {
      clearInterval(typingInterval);
      if (props.loop) {
        setTimeout(() => {
          currentTextIndex.value = (currentTextIndex.value + 1) % (Array.isArray(props.text) ? props.text.length : 1);
          typeText(Array.isArray(props.text) ? props.text[currentTextIndex.value] : props.text);
        }, 1000); // Delay before re-typing
      }
    }
  }, props.speed);
};

onMounted(() => {
  typeText(Array.isArray(props.text) ? props.text[currentTextIndex.value] : props.text);
});

onUnmounted(() => {
  clearInterval(typingInterval);
});

watch(() => props.text, (newText) => {
  clearInterval(typingInterval);
  currentTextIndex.value = 0;
  typeText(Array.isArray(newText) ? newText[currentTextIndex.value] : newText);
});
</script>

<template>
  <div :class="cn('typing-animation', className)">
    {{ displayedText }}
  </div>
</template>

<style scoped>
/* Add any specific styles for typing animation if needed */
</style>
