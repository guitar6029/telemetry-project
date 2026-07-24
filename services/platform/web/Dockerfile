FROM node:24-alpine

WORKDIR /src

COPY package.json package-lock.json ./

# run npm install
RUN npm install

# copied everything from /web into workdir
COPY . .

CMD ["npm", "start", "--", "--host", "0.0.0.0"]
