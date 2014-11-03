
ENV['VAGRANT_DEFAULT_PROVIDER'] = 'docker'

$num_instances = 3
http_port = "8080"
akka_port = "2551"

Vagrant.configure("2") do |config|

  (1..$num_instances).each do |i|

    http_port_host = "808%01d" % (i-1)
    akka_port_host = "255%01d" % (i)

    config.vm.define "app%02d" % i do |a|
      a.vm.provider "docker" do |d|
        d.build_dir = "."
        d.ports = [http_port_host + ":" + http_port, akka_port_host + ":" + akka_port]
        d.name = "app%02d" % i
        d.volumes = ["/tmp:/tmp","/tmp/downloads:/downloads"]
        d.env = { "SEED_0_ADDR" => "192.168.1.174", "SEED_0_PORT" => "2551", 
		"SEED_1_ADDR" => "192.168.1.174", "SEED_1_PORT" => "2551", 
		"NODE_ADDR" => "192.168.1.174", "NODE_PORT" => akka_port, 
		"HTTP_PORT" => http_port, 
		"NODE_ADDR" => "192.168.1.174"}
      end
    end

  end

end

