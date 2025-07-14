# ☕ CoffeeApp API

Java Spring Boot ile geliştirilmiş RESTful servis. Kahve siparişi, kullanıcı işlemleri ve yetkilendirme gibi temel özellikleri içerir.

---

## 🚀 Projeyi Yerel Ortamda Çalıştırmak

Aşağıdaki adımlar ile projeyi Docker üzerinden PostgreSQL ile birlikte ayağa kaldırabilir, bağımlılıkları yükleyip doğrudan geliştirmeye başlayabilirsiniz.

---

## 📦 Gereksinimler

- [Java 17+] 
- [Maven 3.8+]
- [Docker] 
- [Git] 

---


🔧 Kurulum

### 1. Reponun klonlanması

git clone https://github.com/kullaniciAdi/coffeAppApi.git
cd coffeAppApi

---

## 🐘 Veritabanı: Docker ile PostgreSQL Kurulumu

Aşağıdaki komutu terminalde çalıştırarak PostgreSQL container’ını başlatabilirsiniz:

docker run --name coffe-postgres \
  -e POSTGRES_USER=coffe \
  -e POSTGRES_PASSWORD=coffe \
  -e POSTGRES_DB=coffe \
  -p 5432:5432 \
  -d postgres:latest

## Bağımlılıkların yüklenmesi

mvn clean install
mvn spring-boot:run
