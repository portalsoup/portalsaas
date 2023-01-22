terraform {
  required_providers {
    digitalocean = {
      source = "digitalocean/digitalocean"
      version = "~> 2.0"
    }
  }
}

provider "digitalocean" {
  token = var.do_token
}
resource "digitalocean_droplet" "portalsaas" {
  image = "ubuntu-18-04-x64"
  name   = "portalsaas"
  region = "nyc3"
  size   = "s-1vcpu-1gb"
  ssh_keys = [data.digitalocean_ssh_key.main.id]
}

resource "digitalocean_project" "portalsaas" {
  name = "portalsaas"
  resources = [digitalocean_droplet.portalsaas.urn]
}