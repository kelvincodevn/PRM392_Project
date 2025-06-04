using DAOs;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace BusinessObjects.DTOs
{
    public class StaffDTO
    {
        public int StaffId { get; set; }

        public int? UserId { get; set; }

        public string? DeliveryRegion { get; set; }

        public string? Status { get; set; }

        public string? VehicleInfo { get; set; }

    }
}
