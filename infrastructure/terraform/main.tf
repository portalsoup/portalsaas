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

# The project definition
resource "digitalocean_project" "portalsaas" {
  name = "portalsaas"
  resources = [digitalocean_droplet.portalsaas.urn, digitalocean_database_cluster.postgres.urn]
}

resource "digitalocean_droplet" "portalsaas" {
  image = "ubuntu-18-04-x64"
  name   = "portalsaas"
  region = "nyc3"
  size   = "s-1vcpu-1gb"
  ssh_keys = [data.digitalocean_ssh_key.main.id]
}

resource "random_string" "cluster-suffix" {
  length           = 4
  min_lower        = 2
  min_numeric      = 2
  special          = false
  override_special = "/@\\ "
}

resource "digitalocean_database_cluster" "postgres" {
  name = "portalsaas-cluster"
  engine = "pg"
  version = "14"
  size = "db-s-1vcpu-1gb"
  region = "nyc3"
  node_count = 1
  tags = []
  private_network_uuid = digitalocean_droplet.portalsaas.vpc_uuid
}

resource "digitalocean_database_db" "main" {
  cluster_id = digitalocean_database_cluster.postgres.id
  name       = "portalsaas"
}
