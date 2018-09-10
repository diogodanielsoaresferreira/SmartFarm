from confluent_kafka import Consumer, KafkaError


c = Consumer({
    'bootstrap.servers': 'deti-engsoft-01.ua.pt:9092',
    'group.id': 'mygroup',
    'default.topic.config': {
        'auto.offset.reset': 'smallest'
    }
})


c.subscribe(['actuator_value'])

while True:
    msg = c.poll(1.0)

    if msg is None:
        continue
    if msg.error():
        if msg.error().code() == KafkaError._PARTITION_EOF:
            continue
        else:
            print(msg.error())
            break

    print('Received message: {}'.format(msg.value().decode('utf-8')))

c.close()
