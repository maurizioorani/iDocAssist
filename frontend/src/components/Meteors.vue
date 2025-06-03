<template>
  <div class="meteors-container">
    <div 
      v-for="meteor in meteors" 
      :key="meteor.id"
      class="meteor"
      :style="{ 
        '--x-start': `${meteor.startX}%`, 
        '--x-end': `${meteor.endX}%`, 
        '--y-start': `${meteor.startY}%`, 
        '--y-end': `${meteor.endY}%`, 
        '--size': `${meteor.size}px`, 
        '--opacity': meteor.opacity,
        '--duration': `${meteor.duration}s`,
        '--delay': `${meteor.delay}s`
      }"
    ></div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';

const props = defineProps({
  count: {
    type: Number,
    default: 5
  }
});

const meteors = ref([]);

onMounted(() => {
  generateMeteors();
});

const generateMeteors = () => {
  meteors.value = Array.from({ length: props.count }, (_, i) => ({
    id: i,
    startX: randomBetween(10, 90), 
    endX: randomBetween(10, 90),
    startY: randomBetween(-20, 0),
    endY: randomBetween(80, 100),
    size: randomBetween(2, 6),
    opacity: randomBetween(0.6, 1, true),
    duration: randomBetween(2, 6, true),
    delay: randomBetween(0, 15, true),
  }));
};

function randomBetween(min, max, floating = false) {
  if (floating) {
    return min + Math.random() * (max - min);
  }
  return Math.floor(min + Math.random() * (max - min + 1));
}
</script>

<style scoped>
.meteors-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  z-index: -1;
}

.meteor {
  position: absolute;
  width: var(--size);
  height: var(--size);
  background: linear-gradient(90deg, rgba(255,255,255,0) 0%, rgba(120,144,240,0.8) 50%, rgba(75,100,255,0.2) 100%);
  opacity: var(--opacity);
  top: var(--y-start);
  left: var(--x-start);
  border-radius: 50%;
  filter: blur(1px);
  animation: meteor-move var(--duration) ease-out var(--delay) infinite;
}

@keyframes meteor-move {
  0% {
    transform: translate(0, 0) scale(0.3) rotate(0deg);
    opacity: 0;
  }
  10% {
    opacity: var(--opacity);
  }
  100% {
    transform: 
      translate(
        calc((var(--x-end) - var(--x-start)) * 1px), 
        calc((var(--y-end) - var(--y-start)) * 1px)
      ) 
      scale(0.1) 
      rotate(45deg);
    opacity: 0;
  }
}
</style>
