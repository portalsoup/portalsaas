output "postgres-id" {
  value = digitalocean_database_cluster.postgres.id
}

output "postgres-private_host" {
  value = digitalocean_database_cluster.postgres.private_host
}

output "postgres-host" {
  value = digitalocean_database_cluster.postgres.host
}

output "postgres-database" {
  value = digitalocean_database_cluster.postgres.database
}

output "postgres-user" {
  value = digitalocean_database_cluster.postgres.user
}

output "postgres-password" {
  value = digitalocean_database_cluster.postgres.password
  sensitive = true
}