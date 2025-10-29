/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        'linkedin-blue': '#0A66C2',
        'linkedin-dark': '#000000',
      },
    },
  },
  plugins: [],
};
