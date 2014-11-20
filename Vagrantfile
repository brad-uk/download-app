
ENV['VAGRANT_DEFAULT_PROVIDER'] = 'docker'

$num_instances = 3
http_port = "8080"
akka_port = "2551"

Vagrant.configure("2") do |config|

  #seed node, always on host port 2551
  config.vm.define "app-seed" do |a|
    a.vm.provider "docker" do |d|
      d.image="app"
      d.ports = ["8080:8080", "2551:2551"]
      d.name = "app-seed"
      d.volumes = ["/tmp:/tmp","/tmp/downloads:/downloads"]
      d.env = { "SEED_0_ADDR" => "192.168.1.10", "SEED_0_PORT" => "2551", 
                "SEED_1_ADDR" => "192.168.1.10", "SEED_1_PORT" => "2552", 
		"NODE_ADDR" => "192.168.1.10", "NODE_PORT" => akka_port, 
		"HTTP_PORT" => http_port}
    end
  end

  #non-seed nodes, host bond port incremented for each instance
  (1..$num_instances).each do |i|

    http_port_host = "808%01d" % (i)
    akka_port_host = "255%01d" % (i+1)

    config.vm.define "app%02d" % i do |a|
      a.vm.provider "docker" do |d|
        d.image="app"
        d.ports = [http_port_host + ":" + http_port, akka_port_host + ":" + akka_port_host]
        d.name = "app%02d" % i
        d.volumes = ["/tmp:/tmp","/tmp/downloads:/downloads"]
        d.env = { "SEED_0_ADDR" => "192.168.1.10", "SEED_0_PORT" => "2551", 
		"SEED_1_ADDR" => "192.168.1.10", "SEED_1_PORT" => "2552", 
		"NODE_ADDR" => "192.168.1.10", "NODE_PORT" => akka_port_host, 
		"HTTP_PORT" => http_port}
      end
    end

  end

end

