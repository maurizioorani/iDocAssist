<script setup>
import { ref, onMounted, onUnmounted, computed, h } from 'vue';
// import { motion } from 'motion'; // Assuming motion is installed and can be imported like this
import { cn } from '@/lib/utils'; // Assuming cn utility is available

const props = defineProps({
  perspective: {
    type: Number,
    default: 100,
  },
  beamsPerSide: {
    type: Number,
    default: 3,
  },
  beamSize: {
    type: Number,
    default: 5,
  },
  beamDelayMax: {
    type: Number,
    default: 3,
  },
  beamDelayMin: {
    type: Number,
    default: 0,
  },
  beamDuration: {
    type: Number,
    default: 3,
  },
  gridColor: {
    type: String,
    default: 'var(--border)',
  },
  className: {
    type: String,
    default: '',
  },
});

const Beam = ({ width, x, delay, duration }) => {
  const hue = Math.floor(Math.random() * 360);
  const ar = Math.floor(Math.random() * 10) + 1;

  const style = computed(() => ({
    '--x': `${x}`,
    '--width': `${width}`,
    '--aspect-ratio': `${ar}`,
    '--background': `linear-gradient(hsl(${hue} 80% 60%), transparent)`,
  }));

  // onMounted(() => {
  //   motion(
  //     `[data-beam-x="${x}"][data-beam-width="${width}"]`,
  //     { y: ["100cqmax", "-100%"], x: ["-50%", "-50%"] },
  //     {
  //       duration,
  //       delay,
  //       repeat: Infinity,
  //       ease: "linear",
  //     }
  //   );
  // });

  return h('div', {
    class: `absolute left-[var(--x)] top-0 [aspect-ratio:1/var(--aspect-ratio)] [background:var(--background)] [width:var(--width)]`,
    style: style.value,
    'data-beam-x': x,
    'data-beam-width': width,
  });
};

const generateBeams = () => {
  const beams = [];
  const cellsPerSide = Math.floor(100 / props.beamSize);
  const step = cellsPerSide / props.beamsPerSide;

  for (let i = 0; i < props.beamsPerSide; i++) {
    const x = Math.floor(i * step);
    const delay = Math.random() * (props.beamDelayMax - props.beamDelayMin) + props.beamDelayMin;
    beams.push({ x, delay });
  }
  return beams;
};

const topBeams = computed(generateBeams);
const rightBeams = computed(generateBeams);
const bottomBeams = computed(generateBeams);
const leftBeams = computed(generateBeams);

const gridStyles = computed(() => ({
  '--perspective': `${props.perspective}px`,
  '--grid-color': props.gridColor,
  '--beam-size': `${props.beamSize}%`,
}));
</script>

<template>
  <div :class="cn('relative rounded border p-20', className)">
    <div
      :style="gridStyles"
      class="pointer-events-none absolute left-0 top-0 size-full overflow-hidden [clipPath:inset(0)] [container-type:size] [perspective:var(--perspective)] [transform-style:preserve-3d]"
    >
      <!-- top side -->
      <div class="absolute z-20 [transform-style:preserve-3d] [background-size:var(--beam-size)_var(--beam-size)] [background:linear-gradient(var(--grid-color)_0_1px,_transparent_1px_var(--beam-size))_50%_-0.5px_/var(--beam-size)_var(--beam-size),linear-gradient(90deg,_var(--grid-color)_0_1px,_transparent_1px_var(--beam-size))_50%_50%_/var(--beam-size)_var(--beam-size)] [container-type:inline-size] [height:100cqmax] [transform-origin:50%_0%] [transform:rotateX(-90deg)] [width:100cqi]">
        <Beam v-for="(beam, index) in topBeams" :key="`top-${index}`" :width="`${beamSize}%`" :x="`${beam.x * beamSize}%`" :delay="beam.delay" :duration="beamDuration" />
      </div>
      <!-- bottom side -->
      <div class="absolute top-full [transform-style:preserve-3d] [background-size:var(--beam-size)_var(--beam-size)] [background:linear-gradient(var(--grid-color)_0_1px,_transparent_1px_var(--beam-size))_50%_-0.5px_/var(--beam-size)_var(--beam-size),linear-gradient(90deg,_var(--grid-color)_0_1px,_transparent_1px_var(--beam-size))_50%_50%_/var(--beam-size)_var(--beam-size)] [container-type:inline-size] [height:100cqmax] [transform-origin:50%_0%] [transform:rotateX(-90deg)] [width:100cqi]">
        <Beam v-for="(beam, index) in bottomBeams" :key="`bottom-${index}`" :width="`${beamSize}%`" :x="`${beam.x * beamSize}%`" :delay="beam.delay" :duration="beamDuration" />
      </div>
      <!-- left side -->
      <div class="absolute left-0 top-0 [transform-style:preserve-3d] [background-size:var(--beam-size)_var(--beam-size)] [background:linear-gradient(var(--grid-color)_0_1px,_transparent_1px_var(--beam-size))_50%_-0.5px_/var(--beam-size)_var(--beam-size),linear-gradient(90deg,_var(--grid-color)_0_1px,_transparent_1px_var(--beam-size))_50%_50%_/var(--beam-size)_var(--beam-size)] [container-type:inline-size] [height:100cqmax] [transform-origin:0%_0%] [transform:rotate(90deg)_rotateX(-90deg)] [width:100cqh]">
        <Beam v-for="(beam, index) in leftBeams" :key="`left-${index}`" :width="`${beamSize}%`" :x="`${beam.x * beamSize}%`" :delay="beam.delay" :duration="beamDuration" />
      </div>
      <!-- right side -->
      <div class="absolute right-0 top-0 [transform-style:preserve-3d] [background-size:var(--beam-size)_var(--beam-size)] [background:linear-gradient(var(--grid-color)_0_1px,_transparent_1px_var(--beam-size))_50%_-0.5px_/var(--beam-size)_var(--beam-size),linear-gradient(90deg,_var(--grid-color)_0_1px,_transparent_1px_var(--beam-size))_50%_50%_/var(--beam-size)_var(--beam-size)] [container-type:inline-size] [height:100cqmax] [width:100cqh] [transform-origin:100%_0%] [transform:rotate(-90deg)_rotateX(-90deg)]">
        <Beam v-for="(beam, index) in rightBeams" :key="`right-${index}`" :width="`${beamSize}%`" :x="`${beam.x * beamSize}%`" :delay="beam.delay" :duration="beamDuration" />
      </div>
      <slot></slot>
    </div>
  </div>
</template>
