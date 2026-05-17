FROM eclipse-temurin:21-jdk-jammy

LABEL org.opencontainers.image.title="Gl4dius"
LABEL org.opencontainers.image.description="A very powerful tool for All kinds of MITM attacks"
LABEL org.opencontainers.image.authors="Gl4di4tor"
LABEL org.opencontainers.image.source="https://github.com/arVahedi/Gl4dius"
LABEL org.opencontainers.image.licenses="GPL-3.0"

ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update && apt-get install -y \
    bash \
    curl \
    wget \
    iproute2 \
    iputils-ping \
    net-tools \
    dnsutils \
    tcpdump \
    iptables \
    nftables \
    procps \
    vim \
    less \
    lsof \
    ca-certificates \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /workspace

CMD ["/bin/bash"]
